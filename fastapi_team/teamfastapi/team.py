import os, json, requests
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
import uvicorn
import base64
from pydantic import BaseModel
from fastapi.staticfiles import StaticFiles

import hashlib
import re
from io import BytesIO
from PIL import Image
from sentence_transformers import SentenceTransformer, util
from langchain_core.runnables import RunnableMap
from datetime import date
from langchain_core.output_parsers import JsonOutputParser
from langchain_core.prompts import ChatPromptTemplate
from fastapi.testclient import TestClient  # ✔ 여전히 FastAPI의 공식 경로 (정상 동작함)

import random
import cx_Oracle  # Oracle
from retriever import retriever, document_chain, query_augmentation_chain 
from langchain_core.messages import SystemMessage, HumanMessage
from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from apitool import answer  # ✅ answer 함수 import
from apitool import imggen_gpt
from apitool import describe_scene_gpt_vision
from apitool import is_context_used
from apitool import clean_json_block
from apitool import format_response_sentences
from bs4 import BeautifulSoup
import requests
# ✅ 환경 변수 로드
load_dotenv('./env.txt')
KAKAO_API_KEY = os.getenv("KAKAO_API_KEY")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
SpringBoot_FastAPI_KEY = os.getenv("SpringBoot_FastAPI_KEY")

app = FastAPI()

# ✅ CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ImgRequest(BaseModel):
    image_base64: str

@app.get("/")
def test():
    return {"resort": "FastAPI + OpenAI 연결 정상 동작 중!"}


@app.post("/hospital")
async def hospital_recommend_proc(request: Request):
    try:
        data = await request.json()

        if data.get("SpringBoot_FastAPI_KEY") != SpringBoot_FastAPI_KEY:
            return JSONResponse(status_code=401, content={"error": "KEY 인증 실패"})

        query = data.get("query", "").strip()
        animal = data.get("animal", "").strip()

        if not query:
            return JSONResponse(status_code=400, content={"error": "query 누락"})

        # ✅ Kakao API 호출
        headers = {
            "Authorization": f"KakaoAK {KAKAO_API_KEY}"
        }

        params = {
            "query": query,
            "category_group_code": "HP8",  # 병원
            "size": 10
        }

        kakao_response = requests.get(
            "https://dapi.kakao.com/v2/local/search/keyword.json",
            headers=headers,
            params=params
        )

        print("📡 카카오 응답 상태:", kakao_response.status_code)
        kakao_data = kakao_response.json()

        hospital_list = []
        for doc in kakao_data.get("documents", []):
            if "동물병원" in doc.get("category_name", ""):
                hospital_list.append({
                    "name": doc.get("place_name"),
                    "address": doc.get("road_address_name") or doc.get("address_name"),
                    "tel": doc.get("phone", "정보 없음"),
                    "homepage": doc.get("place_url")
                })

        # ✅ 병원 리스트가 비어있으면 바로 반환
        if not hospital_list:
            return JSONResponse(content={"result": []}, status_code=200)

        # ✅ GPT 추천
        if animal:
            role = "너는 특수동물 병원 추천 전문가야."
            format_instruction = """
            {
              "result": [
                {
                  "name": "병원명",
                  "address": "주소",
                  "tel": "전화번호",
                  "homepage": "홈페이지"
                }
              ]
            }
            """
            prompt = f"""
아래는 '{query}' 키워드로 검색된 동물병원 리스트야.
이 중에서 '{animal}'을 진료할 확률이 높은 병원 **5개만 골라줘.**
아무 설명 없이 JSON 형식만 응답해.

[입력 병원 목록]
{json.dumps(hospital_list, ensure_ascii=False, indent=2)}
"""

            try:
                gpt_result = answer(role, prompt, format_instruction)
                print("🧠 GPT 응답 원문:", gpt_result)
                result = gpt_result.get("result", [])[:5]

            except Exception as e:
                print("❌ GPT 응답 파싱 오류:", e)
                return JSONResponse(status_code=500, content={
                    "error": "GPT 파싱 오류",
                    "detail": str(e),
                    "raw_response": str(gpt_result)
                })
        else:
            # 🐾 동물 정보 없으면 그냥 상위 3개 반환
            result = hospital_list[:5]

        try:
            save_response = requests.post(
                "http://localhost:9093/hospital/save",  # Spring 컨트롤러 주소
                headers={"Content-Type": "application/json"},
                json=result  # 추천된 병원 리스트 전체
            )
            print("✅ 저장 응답:", save_response.text)
        except Exception as e:
            print("❌ 병원 저장 실패:", e)
        return JSONResponse(content={"result": result}, status_code=200)

    except Exception as e:
        import traceback
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "서버 오류", "detail": str(e)})

@app.post("/memory")
async def illustrate_image(req: ImgRequest):
    try:
        # 이미지 디코딩
        image_data = base64.b64decode(req.image_base64)
        image = Image.open(BytesIO(image_data))

        # 원본 이미지 저장
        if not os.path.exists("./static/upload"):
            os.makedirs("./static/upload")
        raw_path = "./static/upload/input.jpg"
        image.save(raw_path)
        
        base64_str = base64.b64encode(image_data).decode("utf-8")

        description = describe_scene_gpt_vision(base64_str)
        prompt = f"""
        다음은 이미지 분석 결과야:
        {description}

        위 장면을 애니메이션 일러스트로 변환해줘
        단, 출력되는 이미지의 해상도와 파일 형식(JPG, PNG 등)은 원본과 동일하게 유지해줘.
        포맷이 바뀌거나 비표준 형식이 되면 안 돼.
        """
        print("description", description)

        image_url = imggen_gpt(prompt)  # 실제 생성된 경로 반환

        return {"image_url": image_url}

    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})

@app.post("/guide")
async def recommend_pet(request: Request):
    data = await request.json()
    
    if data.get("SpringBoot_FastAPI_KEY") != SpringBoot_FastAPI_KEY:
        return JSONResponse(status_code=401, content={"error": "인증 실패"})

    # 사용자 응답 추출
    age = data.get("age", "")
    experience = data.get("experience", "")
    personality = data.get("personality", "")
    environment = data.get("environment", "")
    condition = data.get("condition", "")

    # 프롬프트 구성
    format_string = """
    {
        "추천": "피그미 염소",
        "설명": "작고 친근하며 농촌 주거지에 적합한 종입니다.",
        "현실성": "입양 가능",
        "주의사항": "실외 공간이 필요하며 지자체 조례 확인 필요"
    }
    """

    prompt = f"""
    당신은 한국에서 활동 중인 특수반려동물 입양 컨설턴트입니다.

    아래 사용자 설문을 바탕으로,  
    📌 **한국에서 사육 가능한 현실적인 종** 중  
    📌 **사용자의 나이, 경험, 성격, 환경, 특이사항**에 가장 적합한  
    단, 동일한 상황에서도 결과가 항상 같을 필요는 없습니다.

    다음 카테고리 중에서 추천 가능합니다:
    - 파충류 (예: 레오파드게코, 비어디드래곤, 크레스티드게코, 콘스네이크, 팬서 카멜레온 등)
    - 조류 (예: 코뉴어, 모란앵무, 왕관앵무, 카나리아, 회색앵무 등)
    - 포유류 (예: 슈가글라이더, 페럿, 친칠라, 토끼, 기니피그, 스컹크 등)
    - 양서류 (예: 화이트트리프록, 아마존 밀크개구리, 도롱뇽, 파이어벨리 뉴트 등)
    - 절지류 (예: 타란툴라, 전갈 , 프레잉맨티스, 사마귀등)
    - 어류 (예: 구피, 코리도라스, 베타, 디스커스, 엔젤피시, 플래티 등)
    - 기타 특이한 숨숨이 (예: 애완 거위, 미니 당나귀, 피그미 염소 등)
    
    ※ 아래 기준을 꼭 지켜주세요:
    - 한국에서 실제로 입양/사육이 가능한 종만 추천
    - 법적 제한이 있거나 희귀하여 현실적으로 어려운 종은 제외
    - 사용자의 모든 조건을 고려한 1종만 추천
    - 너무 흔한 강아지/고양이는 제외해주세요.

    [사용자 설문 결과]
    - 나이: {age}
    - 반려동물 경험: {experience}
    - 성격: {personality}
    - 환경: {environment}
    - 사용자 요청사항: {condition}
    
    [응답 형식 예시]
    {format_string}
    """

    try:
        result = answer(
            role="특수 반려동물 추천 전문가",
            prompt=prompt,
            format=format_string,
            )
        return {"result": result}
    except Exception as e:
        import traceback
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "GPT 호출 실패", "detail": str(e)})

# ✅ 프롬프트: context를 명확히 문서로 전달
source_check_prompt = PromptTemplate.from_template("""
다음은 사용자의 질문과 AI의 응답입니다.

[질문]
{question}

[AI 응답]
{response}

[참고한 문서 목록]
{context}

출처를 다음 중 하나로 엄격하게 판별하세요:

- 응답에 포함된 **단 하나의 키워드, 문장, 문맥**이라도 문서 내에 존재하면 반드시 "내부룰셋"으로 판단하세요.
- 일반 지식 여부는 고려하지 마세요. 문서에 등장한 정보와 겹치기만 해도 "내부룰셋"입니다.

정확히 다음 형식의 JSON만 출력하세요:
{{ "source_type": "내부룰셋" }}
또는
{{ "source_type": "GPT" }}
""")

# ✅ Runnable 구성: context 문서 내용을 텍스트로 변환
source_check_chain = RunnableMap({
    "question": lambda input: input["question"],
    "response": lambda input: input["response"],
    "context": lambda input: "\n\n".join(
        f"[문서 {i+1}]\n{doc.page_content}"
        for i, doc in enumerate(input.get("context", []))
    )
}) | source_check_prompt | ChatOpenAI(model="gpt-4o", temperature=0)


@app.post("/chat")
async def ai_chat(request: Request):
    try:
        data = await request.json()

        # 🔐 인증
        if data.get("SpringBoot_FastAPI_KEY") != SpringBoot_FastAPI_KEY:
            return JSONResponse(status_code=401, content={"error": "인증 실패"})

        user_question = data.get("question", "").strip()
        if not user_question:
            return JSONResponse(status_code=400, content={"error": "질문이 없습니다."})

        # 🧠 역할 설정
        system_msg = SystemMessage(content="너는 특수반려동물 관련 AI 상담사야. 친절하고 핵심만 알려줘.")
        messages = [system_msg, HumanMessage(content=user_question)]

        # 🔍 Step 1: 질문 보강
        augmented_query = query_augmentation_chain.invoke({
            "messages": messages,
            "query": user_question
        })

        # 📚 Step 2: 문서 검색
        relevant_docs = retriever.invoke(augmented_query)

        # 💬 Step 3: 응답 생성
        response = document_chain.invoke({
            "context": relevant_docs,
            "messages": messages
        })

        # 🧭 Step 4: 출처 판단
        source_result = source_check_chain.invoke({
            "question": user_question,
            "response": response,
            "context": relevant_docs
        })

        try:
            print("🧠 LLM 판단 원본:", source_result.content)

            cleaned_json = clean_json_block(source_result.content)
            source_type = json.loads(cleaned_json).get("source_type", "GPT")

            print("📌 파싱 후 source_type:", source_type)
            if source_type == "GPT":
                response = format_response_sentences(response)
                if is_context_used(response, relevant_docs):
                    print("🔥 내부룰셋으로 보정됨")
                    source_type = "내부룰셋"

        except Exception as e:
            print("❌ 출처 판단 오류:", str(e))
            source_type = "GPT"
                
        # 🏷️ Step 5: 키워드 추출
        keyword_prompt = f"""
        다음 질문과 AI의 응답을 참고하여 핵심 키워드를 2~5개 추출해주세요.

        - 질문과 응답의 주요 개념을 반영한 **명사 중심의 키워드**만 추출합니다.
        - **동의어나 유사 표현은 하나로 통일**합니다. (예: 활동 저하, 활동 감소 → 활동저하)
        - **중복 표현은 제거**하고, **간결하게 정리**합니다.
        - **콤마(,)로 구분된 문자열**로 출력합니다.

        [질문]
        {user_question}

        [AI 응답]
        {response}

        아래 형식의 JSON으로 응답해줘:
        """
        keyword_format = '{ "symptom_tags":"키워드1, 키워드2, 키워드3" }'
        keyword_result = answer("증상 추출기", keyword_prompt, keyword_format)
        symptom_tags = keyword_result.get("symptom_tags", "").strip() or "기타"

        return {
            "result": {
                "answer": response,
                "symptom_tags": symptom_tags,
                "source_type": source_type
            }
        }

    except Exception as e:
        import traceback
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "GPT 응답 오류", "detail": str(e)})


model = SentenceTransformer("all-MiniLM-L6-v2")  # 빠르고 성능 괜찮은 모델
    
# 요약 및 감정 분석 - 캘린더용
@app.post("/calendar/summary")
async def calendar_summary(request: Request):
    try:
        data = await request.json()

        print("📦 요청된 데이터:", data)  # ✅ 전체 요청 확인

        # 🔐 인증 확인
        SpringBoot_FastAPI_KEY = data.get("SpringBoot_FastAPI_KEY")
        print("🔐 받은 키:", SpringBoot_FastAPI_KEY)  # ✅ 키 확인
        if SpringBoot_FastAPI_KEY != SpringBoot_FastAPI_KEY:
            print("❌ 인증 실패")
            return JSONResponse(status_code=401, content={"error": "KEY 인증 실패"})

        content = data.get("content", "").strip()
        print("🧾 전달받은 content:", content)  # ✅ content 확인

        if not content:
            print("⚠️ content 비어있음")
            return JSONResponse(status_code=400, content={"error": "내용 없음"})

        # 🧠 GPT 프롬프트 구성
        role = "너는 감정 분석과 요약을 잘하는 비서야. 내용 요약과 감정을 분석해줘."

        prompt = f"""
        다음 내용을 100자 이내로 요약하고, 상담 내용의 감정이 '긍정'이면'1', '부정'이면 '0'으로 반드시 숫자로 분류해줘.
        [내용]: {content}

        결과는 반드시 아래 JSON 형식으로 반환해:
        {{
          "summary": "요약된 문장",
          "emotion": "긍정:1" 또는 "부정:0"
        }}
        """

        format_instruction = """
        {
          "summary": "하루 동안 즐거운 시간을 보냄",
          "emotion": 1
        }
        """

        # ✅ GPT 호출
        gpt_response = answer(role=role, prompt=prompt, format=format_instruction)

        print("✅ GPT 응답 결과:", gpt_response)  # ✅ 응답 확인

        summary = gpt_response.get("summary", "")
        emotion_text = gpt_response.get("emotion", "부정")

        # 1: 긍정, 0: 부정
        emotion = 1 if str(emotion_text).strip() in ["1", "긍정", "positive"] else 0

        return JSONResponse(content={
            "summary": summary,
            "emotion": emotion
        }, status_code=200)

    except Exception as e:
        import traceback
        print("❌ 예외 발생:")
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "GPT 호출 실패", "detail": str(e)})

# 요약 및 감정 분석 - 상담용
@app.post("/ai_consult/summary")
async def calendar_chat(request: Request):
    try:
        data = await request.json()

        print("📦 요청된 데이터:", data)  # ✅ 전체 요청 확인

        # 🔐 인증 확인
        SpringBoot_FastAPI_KEY = data.get("SpringBoot_FastAPI_KEY")
        print("🔐 받은 키:", SpringBoot_FastAPI_KEY)  # ✅ 키 확인
        if SpringBoot_FastAPI_KEY != SpringBoot_FastAPI_KEY:
            print("❌ 인증 실패")
            return JSONResponse(status_code=401, content={"error": "KEY 인증 실패"})

        content = data.get("content", "").strip()
        print("🧾 전달받은 content:", content)  # ✅ content 확인

        if not content:
            print("⚠️ content 비어있음")
            return JSONResponse(status_code=400, content={"error": "내용 없음"})

        # 🧠 GPT 프롬프트 구성
        role = "너는 감정 분석과 요약을 잘하는 비서야. 내용 요약과 감정을 분석해줘."

        prompt = f"""
        다음 내용을 100자 이내로 요약하고, 상담 내용의 감정이 '긍정'이면'1', '부정'이면 '0'으로 반드시 숫자로 분류해줘.
        [내용]: {content}

        결과는 반드시 아래 JSON 형식으로 반환해:
        {{
          "summary": "요약된 문장",
          "emotion": "긍정:1" 또는 "부정:0"
        }}
        """

        format_instruction = """
        {
          "summary": "하루 동안 즐거운 시간을 보냄",
          "emotion": 1
        }
        """

        # ✅ GPT 호출
        gpt_response = answer(role=role, prompt=prompt, format=format_instruction)

        print("✅ GPT 응답 결과:", gpt_response)  # ✅ 응답 확인

        summary = gpt_response.get("summary", "")
        emotion_text = gpt_response.get("emotion", "부정")

        # 1: 긍정, 0: 부정
        emotion = 1 if str(emotion_text).strip() in ["1", "긍정", "positive"] else 0

        return JSONResponse(content={
            "summary": summary,
            "emotion": emotion
        }, status_code=200)

    except Exception as e:
        import traceback
        print("❌ 예외 발생:")
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "GPT 호출 실패", "detail": str(e)})


@app.post("/find_similar_ai")
async def find_similar_ai(request: Request):
    data = await request.json()

    if data.get("SpringBoot_FastAPI_KEY") != SpringBoot_FastAPI_KEY:
        return JSONResponse(status_code=401, content={"error": "인증 실패"})

    user_question = data.get("question", "")
    user_symptom_tags = data.get("symptom_tags", "")
    history = data.get("history", [])
    target_consultno = data.get("consultno")  # 현재 글 번호

    if not user_question or not history:
        return JSONResponse(content={"similars": []})

    # 🔹 태그 분리
    user_tags = set(user_symptom_tags.replace(",", " ").split())

    user_text = f"{user_question} {user_symptom_tags}"
    user_vec = model.encode(user_text, convert_to_tensor=True)

    scores = []
    for h in history:
        if h["consultno"] == target_consultno:
            continue  # 현재 상담은 제외

        # 🔹 태그 겹침 확인
        h_tags = set(h.get("symptom_tags", "").replace(",", " ").split())
        if not user_tags.intersection(h_tags):
            continue  # 하나도 안 겹치면 제외 ❌

        history_text = f"{h['question']} {h.get('symptom_tags', '')}"
        history_vec = model.encode(history_text, convert_to_tensor=True)
        similarity = util.cos_sim(user_vec, history_vec).item()
        
        print("✅ 유사도:", similarity)

        if similarity >= 0.75:
            scores.append((h['consultno'], similarity))

    scores.sort(key=lambda x: x[1], reverse=True)
    top_consults = [{"consultno": cno, "similarity": round(sim, 3)} for cno, sim in scores[:3]]

    return JSONResponse(content={"similars": top_consults})



# ✅ 키워드 추출 기능
# ✅ 모델 및 체인 구성
llm = ChatOpenAI(model="gpt-4o", temperature=0)

prompt_keyword = PromptTemplate.from_template("""
너는 특수 반려동물 상담 기록에서 핵심 증상 키워드를 잘 추출하는 전문가야.

반드시 핵심 증상 키워드를 **정확히 3개** 뽑아서, 각 키워드 앞에 #을 붙인 후 문자열로 만들어줘.

절대 ```json 같은 코드블럭을 쓰지 마! 딱 JSON 문자열만 반환해.
                                              
문장: {content}

결과는 반드시 아래 JSON 형식처럼 정확히 출력해:
{{
  "symptom_tags": "#증상1 #증상2 #증상3""
}}
""")

chain_keyword = prompt_keyword | llm

# ✅ 요청 본문 모델
class ContentRequest(BaseModel):
    content: str

# ✅ 키워드 추출 API (1개만!)
@app.post("/extract_keywords")
def extract_keywords(data: ContentRequest):
    try:
        print("📨 입력 문장:", data.content)
        result = chain_keyword.invoke({"content": data.content})
        raw_response = result.content.strip()
        print("🧠 GPT 응답:", result.content)
        # ✅ 마크다운 감싸기 제거
        cleaned = re.sub(r"^```json\\n|```$", "", raw_response).strip("` \n")

        # ✅ JSON 파싱
        if not cleaned.startswith("{"):
            cleaned = "{" + cleaned + "}"

       # ✅ 키워드 추출 및 리스트화 (쉼표, # 등 구분자 제거)
        parsed = json.loads(cleaned)
        raw_tags = parsed.get("symptom_tags", "")
        keywords = [k.strip() for k in re.split(r"[#,]", raw_tags) if k.strip()]
        return {"keywords": keywords}

    except Exception as e:
        print("❌ 오류 발생:", str(e))
        return JSONResponse(status_code=500, content={"error": str(e)})

@app.get("/animal/random_pick")
async def pick_random_species():
    species_list = [
        "레오파드게코", "비어디드래곤", "블루텅 스킹크", "크레스티드 게코", "볼 파이톤", "콘 스네이크", "아르마딜로 도마뱀", "캡바라", "머드스킵퍼", "마다가스카르 고슴도치", "카피바라"
        , "러시아 거북", "붉은귀거북", "레오파드 거북", "엑소톨틀", "화이트 트리프로그", "테니렉", "포섬", "미어캣", "타마린 원숭이", "해마", "왈라비", "투칸"
        , "햄스터", "기니피그", "고슴도치", "데구", "슈가 글라이더", "플라잉 리스", "네덜란드 드워프", "베타", "구피", "장수풍뎅이", "가재", "글래스 프로그"
        , "벨리어 토끼", "코뉴어", "왕관앵무", "부엉이", "로리켓", "타란툴라", "엠페러", "사슴벌레", "아프리카 육지달팽이", "플레코", "낙타쥐", "파이어 샐러맨더"
        , "스콜피온", "대벌레", "펜넥 여우", "퀘이커 패럿", "코카투", "로리켓", "점핑 스파이더", "센티페드", "수서거미", "체리새우", "아프리카 너구리"
        , "포메라니안", "말티즈", "시츄", "요크셔", "테리어", "비글", "파그", "프렌치불독", "치와와", "푸들", "폭스 테리어", "라브라도들", "비숑 프리제"
        , "코니쉬 렉스", "데본 렉스", "셀커크 렉스", "벵갈", "브리티시숏헤어", "러시안블루", "랙돌", "메인쿤", "히말라얀", "페르시안", "아프간 하운드", "샤페이", "사모예드", "슈나우저"
        , "뉴트", "제브라 피시", "랩 래트", "카멜레온", "아메리칸 밍크", "미니 돼지", "민물 복어", "아이아이", "먼치킨", "아메리칸 컬", "스코티시 폴드", "엘프 캣", "스핑크스"
        , "", "", "", "러시안 블루", "허스키", "보더콜리", "시바견", "리트리버"
    ]
    picked = random.choice(species_list)
    return JSONResponse(content={"name": picked}, status_code=200)

@app.post("/animal/summary")
async def summarize_animal(request: Request):

    try:
        data = await request.json()

        if data.get("SpringBoot_FastAPI_KEY") != SpringBoot_FastAPI_KEY:
            return JSONResponse(status_code=401, content={"error": "인증 실패"})

        name = data.get("name", "").strip()
        description = data.get("description", "").strip()

        if not name or not description:
            return JSONResponse(status_code=400, content={"error": "이름 또는 설명이 없습니다."})

        prompt = f"""
'{name}'이라는 특수동물에 대해 아래 설명을 참고해서 두 항목을 작성해줘:

1. 요약 (summary): 2~4문장으로 주요 특징 설명.  
  - 성격, 사육 난이도, 먹이, 활동 시간대, 주의점 등을 포함할 수 있어.
  - 감탄문, 명령문은 피하고 친절한 설명문으로 작성해줘.
  
2. 추천 이유 (recommendation): 어떤 사람에게 추천할 수 있는지 자세히 1문장으로 말해줘.

[설명]
{description}

반드시 아래 JSON 형식으로 응답해:
{{
  "summary": "...",
  "recommendation": "..."
}}
        """

        format_instruction = """
        {
          "summary": "레오파드게코는 야행성이고 온순한 성격의 도마뱀입니다. 습도가 낮고 따뜻한 환경을 좋아하며, 혼자서도 잘 지냅니다. 도마뱀 초보자에게 인기가 많습니다.",
          "recommendation": "혼자 키우기 좋은 조용한 반려동물을 찾는 사람에게 적합합니다."
        }
        """

        gpt_result = answer("특수동물 설명자", prompt, format_instruction)

        summary = gpt_result.get("summary", "요약 실패")
        recommendation = gpt_result.get("recommendation", "추천 이유 없음")

        return JSONResponse(content={
            "summary": summary,
            "recommendation": recommendation
        }, status_code=200)

    except Exception as e:
        import traceback
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "GPT 설명 실패", "detail": str(e)})

@app.get("/test/news")
async def test_news():
    payload = {
        "SpringBoot_FastAPI_KEY": "1234",  # 실제 키
        "title": "고양이가 나무 위에 올라갔어요",
        "content": "서울에서 고양이가 구조되었습니다."
    }

    from fastapi.testclient import TestClient
    client = TestClient(app)
    res = client.post("/news/summary", json=payload)
    print("🧪 테스트 응답:", res.status_code, res.json())
    return res.json()


@app.post("/news/summary")
async def summarize_news(request: Request):
    try:
        data = await request.json()

        if data.get("SpringBoot_FastAPI_KEY") != SpringBoot_FastAPI_KEY:
            return JSONResponse(status_code=401, content={"error": "KEY 인증 실패"})

        title = data.get("title", "").strip()
        content = data.get("content", "").strip()
        if not title or not content:
            return JSONResponse(status_code=400, content={"error": "입력 누락"})

        role = "너는 동물 뉴스 분류 및 요약 전문가야."
        format_instruction = """
        {
          "is_animal_related": "YES 또는 NO",
          "summary": "한 문장 요약"
        }
        """
        prompt = f"""
다음 뉴스 제목과 본문이 동물 관련인지 판단하고, 관련 있다면 요약해줘:

[제목]
{title}

[본문]
{content}
"""

        gpt_result = answer(role, prompt, format_instruction)

        # ✅ GPT 결과를 Spring 서버에 저장
        save_payload = {
            "title": title,
            "content": content,
            "summary": gpt_result.get("summary", ""),
            "is_animal": gpt_result.get("is_animal_related", "NO")
        }

        save_response = requests.post(
            "http://localhost:9093/news/save",
            headers={"Content-Type": "application/json"},
            json=save_payload
        )

        print("✅ 저장 응답:", save_response.status_code, save_response.text)
        print("✅ 저장 응답 상태코드:", save_response.status_code)

        return JSONResponse(content=gpt_result, status_code=200)

    except Exception as e:
        import traceback
        traceback.print_exc()
        return JSONResponse(status_code=500, content={"error": "서버 오류", "detail": str(e)})


# ✅ 실행
if __name__ == "__main__":
    uvicorn.run("team:app", host="0.0.0.0", port=8000, reload=True)
