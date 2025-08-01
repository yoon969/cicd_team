# ingest.py

from langchain_community.document_loaders import TextLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import Chroma  # ✅ 경고 해결

# 1. 룰셋 파일 경로
file_path = "data/special_pet_ruleset.txt"

# 2. 텍스트 로딩
loader = TextLoader(file_path, encoding="utf-8")
documents = loader.load()

# 3. 문서 분할
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=100
)
split_docs = text_splitter.split_documents(documents)

# 4. 임베딩 모델
embedding = OpenAIEmbeddings(model="text-embedding-3-large")

# 5. 벡터스토어 생성 및 저장
persist_directory = "C:/kd/ws_python/fastapi_team/teamfastapi/chroma_db_langchain"

vectorstore = Chroma.from_documents(
    documents=split_docs,
    embedding=embedding,
    persist_directory=persist_directory
)

# ✅ 아래 줄을 생략해도 자동 저장되나, 명시적으로 하고 싶다면 아래처럼 합니다
vectorstore.persist()

print("✅ 벡터 저장 완료!")
