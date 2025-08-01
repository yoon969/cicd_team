# 임베딩 모델 선언하기
from langchain_openai import OpenAIEmbeddings

# 텍스트를 벡터로 변환할 Embedding 모델을 초기화합니다.
embedding = OpenAIEmbeddings(model='text-embedding-3-large')

# 언어 모델 불러오기
from langchain_openai import ChatOpenAI

# 질문에 답변하거나 프롬프트를 처리할 대화형 LLM을 초기화합니다.
llm = ChatOpenAI(model="gpt-4o-mini")

# Chroma 벡터스토어 로드
from langchain_community.vectorstores import Chroma 

# 저장된 벡터 데이터가 있는 디렉터리 경로 지정
persist_directory = 'C:/kd/ws_python/fastapi_team/teamfastapi/chroma_db_langchain'

# Chroma 객체를 생성하여 저장된 벡터스토어에 접근합니다.
vectorstore = Chroma(
    persist_directory=persist_directory, # 임베딩되어 저장된 폴더
    embedding_function=embedding  # 검색 시 사용할 임베딩 모델 지정
)

# 검색기(retriever) 생성: k=1 이므로, 유사 문서 상위 1개를 반환합니다.
retriever = vectorstore.as_retriever(k=1)

# 문서 QA 체인 생성
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.output_parsers import StrOutputParser  # 출력값을 문자열로 파싱

# ------------------------------------------------------------------------------------------------
# 프롬프트를 실행하는 Chain 1
# ------------------------------------------------------------------------------------------------
# 시스템 메시지와 사용자 메시지를 합쳐서 프롬프트를 구성합니다.
question_answering_prompt = ChatPromptTemplate.from_messages(
    [
        MessagesPlaceholder(variable_name="messages"),
        (
            "system",
            "아래 context는 특수 반려동물에 대한 설명입니다. 사용자의 질문에 다음 지침을 따라 답변하십시오:\n\n"
            "1. 질문에서 언급된 반려동물 종(species)과 항목(category: 사육환경, 먹이, 습성, 주의할 점 등)을 정확히 파악합니다.\n"
            "2. context에서 해당 종과 항목이 존재할 경우, **해당 항목의 정보만** 요약해 응답합니다.\n"
            "3. 하나의 종에 대해 여러 항목을 질문한 경우는 각각 나눠서 응답합니다.\n"
            "4. context에 해당 종 또는 항목이 없는 경우, 일반적인 특수반려동물 기준으로 간단히 설명합니다. \n"
            "   **절대 아래와 같은 표현은 사용하지 마십시오:**\n"
            "   - '정보가 제공되지 않았습니다'\n"
            "   - '제공할 수 없습니다'\n"
            "   - '관련 정보가 없습니다'\n"
            "   - '알려진 바 없습니다'\n"
            "   - '특정 정보를 제공할 수 없습니다'\n"
            "   - 이와 유사한 '정보 없음'을 암시하는 표현 일체\n"
            "   👉 이 표현이 포함되면 무조건 잘못된 응답입니다.\n"
            "5. 무조건 실제 설명부터 시작하십시오. 불필요한 전제, 인사말, 전체 요약, 반복 표현은 생략하고 **간결하게 핵심만** 전달하십시오.\n"
            "context:\n{context}"
        ),
    ]
)


# LLM과 프롬프트, 출력 파서를 연결해 하나의 체인으로 만듭니다. 결과는 문자열 출력
# create_stuff_documents_chain 함수 사용시 context가 반드시 있어야함.
document_chain = create_stuff_documents_chain(llm, question_answering_prompt) | StrOutputParser()

# ------------------------------------------------------------------------------------------------
# 프롬프트를 실행하는 Chain 2, 사용자의 질문 흐름대로 프롬프트를 수정함
# ------------------------------------------------------------------------------------------------
# 질의 보강(query augmentation) 체인 생성
query_augmentation_prompt = ChatPromptTemplate.from_messages(
    [
        MessagesPlaceholder(variable_name="messages"),  # 기존 대화 내용
        (
            "system",
            "기존의 대화 내용을 활용하여 사용자의 아래 질문의 의도를 파악하여 "
            "명료한 문장의 질문으로 변환하라.\n\n{query}"
        ),
    ]
)
# 보강된 프롬프트를 LLM과 연결하고, 문자열로 파싱합니다.
query_augmentation_chain = query_augmentation_prompt | llm | StrOutputParser()

# 스크립트를 실행하여 AI 기능을 활성화하려면 다음과 같이 호출합니다.