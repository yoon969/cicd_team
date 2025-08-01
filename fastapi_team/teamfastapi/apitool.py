import os
import time
import json
import random
import requests  # web 접속
from PIL import Image # 파이썬 이미지 처리, pip install pillow
from io import BytesIO # byte 입출력
import matplotlib.pyplot as plt # 이미지 출력
from datetime import datetime
import base64
from difflib import SequenceMatcher

# import openai  # 0.28.0
from openai import OpenAI
import re
# openai.api_key='키를 직접 지정하는 경우(권장 아님)' 
# os.environ['OPENAI_API_KEY'] = '키를 직접 지정하는 경우(권장 아님)'

client = OpenAI(
  api_key=os.getenv('OPENAI_API_KEY')
)

# OpenAI API 사용함수
# role: GPT 역활  예) 너는 문화 해설가야
# prompt: 질문 메시지
# format='': 출력 세부 형식, 파라미터 전달이 안되면 아무 값도 사용하지 않는다는 선언
# llm='gpt-4.1-nano, gpt-4.1-mini'
# output='json': 출력 형식
def answer(role, prompt, format, llm='gpt-4.1-nano', output='json'):
    
    if output.lower() == 'json': # 출력 형식이 json인 경우
      response = client.chat.completions.create(
          model=llm,
          messages=[
              {
                  'role': 'system',
                  'content': role
              },
              {
                  'role': 'user',
                  'content': prompt + '\n\n출력 형식(json): ' + format
              }
          ],
          n=1,             # 응답수, 다양한 응답 생성 가능
          max_tokens=3000, # 응답 생성시 최대 1000개의 단어 사용
          temperature=0.7,   # 창의적인 응답여부, 값이 클수록 확률에 기반한 창의적인 응답이 생성됨
          response_format= { "type":"json_object" }
      )
    else: # 출력 형식이 json이 아닌 경우
      response = client.chat.completions.create(
          model=llm,
          messages=[
              {
                  'role': 'system',
                  'content': role
              },
              {
                  'role': 'user',
                  'content': prompt
              }
          ],
          n=1,             # 응답수, 다양한 응답 생성 가능
          max_tokens=3000, # 응답 생성시 최대 1000개의 단어 사용
          temperature=0    # 창의적인 응답여부, 값이 클수록 확률에 기반한 창의적인 응답이 생성됨
      )
   
    return json.loads(response.choices[0].message.content) # str -> json
  
  
  # save_dir: 생성된 이미지 저장 폴더
from datetime import datetime
import random, os, base64
from PIL import Image
import io  # 🔹 base64 → Image 변환을 위해 필요

def imggen_gpt(prompt, model="gpt-image-1", size="1024x1024", quality="medium",
               save_dir=r'C:/kd/deploy/team1/memory/storage'):
    
    response = client.images.generate(
        prompt=prompt,
        model=model,
        size=size,
        quality=quality,
    )
    
    image_base64 = response.data[0].b64_json
    image_bytes = base64.b64decode(image_base64)

    # 🔽 내부 포맷을 JPEG로 확실하게 변환
    image = Image.open(io.BytesIO(image_bytes)).convert("RGB")

    now = datetime.now()
    date_time_string = now.strftime("%Y%m%d%H%M%S")
    random_number = random.randint(1, 1000)

    if not os.path.exists(save_dir):
        os.makedirs(save_dir)

    file_base = f"{date_time_string}_{random_number}"
    file_path = os.path.join(save_dir, f"{file_base}.jpg")
    
    # 🔽 실제 JPEG 포맷으로 저장
    image.save(file_path, format="JPEG")

    return f"/memory/storage/{file_base}.jpg"




# image -> Text
def encode_image(image_path):
  with open(image_path, "rb") as image_file:
    return base64.b64encode(image_file.read()).decode('utf-8')

def encode_image_bytes(image_bytes):
    return base64.b64encode(image_bytes).decode('utf-8')

def describe_scene_gpt_vision(base64_img: str) -> str:
    prompt = """
이 이미지를 가능한 한 **구체적**으로 묘사해줘.

반드시 다음 항목들을 포함해 설명해줘:
- 동물의 **종류** (포유류, 파충류, 양서류, 거미류, 조류 등 구분 포함)
- **신체적 특징** (귀, 눈, 다리, 꼬리, 촉수 등 형태, 위치, 각도)
- **자세와 행동** (앉아 있음, 엎드림, 다리의 위치, 기어감, 도약 등)
- **시선 방향** (정면, 대각선, 위/아래 등 명확히)
- **표정이나 느낌** (공격성, 경계, 편안함 등)
- **배경 환경** (바닥 종류, 주변 물건, 실내/실외 여부 등)
- **사람이 있을 경우**: 사람의 표정, 자세, 시선 방향, 동물과의 상호작용도 함께 묘사

💡 가능하다면 "이 동물은 무엇 같다"는 식의 **유추 표현**은 빼고,
**관찰 가능한 사실만 객관적으로 1문단으로 요약**해줘.
"""

    response = client.chat.completions.create(
        model="gpt-4o",  # 또는 "gpt-4-vision-preview"
        messages=[
            {
                "role": "user",
                "content": [
                    {"type": "text", "text": prompt.strip()},
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{base64_img}"}}
                ]
            }
        ],
        max_tokens=700
    )

    return response.choices[0].message.content.strip()

def is_context_used(response: str, context_docs: list) -> bool:
    combined_context = "\n".join(doc.page_content.lower() for doc in context_docs if doc.page_content)

    similarity = SequenceMatcher(None, response.lower(), combined_context).ratio()

    print("✅ 유사도:", similarity)

    return similarity >= 0.6

def clean_json_block(text: str) -> str:
    # 마크다운 코드블록(```json ... ```) 제거
    return re.sub(r"```(?:json)?\n(.*)\n```", r"\1", text, flags=re.DOTALL).strip()

def format_response_sentences(response: str) -> str:
    """
    응답 문장을 마침표 기준으로 나누어 줄바꿈 추가
    단, 마침표 뒤에 숫자/문자 없이 바로 이어질 경우만 적용
    """
    sentences = re.split(r'(?<=[.?!])\s+', response.strip())
    return "\n".join(sentences)