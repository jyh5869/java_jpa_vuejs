import re
import os
import sys
import json

import tensorflow as tf
import numpy as np

import Levenshtein as Levenshtein

from tensorflow.keras.layers import Embedding, LSTM, Dense, Input
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences

from sklearn.metrics.pairwise import cosine_similarity

# 주소 데이터
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'  # 파일 경로를 적절히 수정하세요

# 1. 도로명 데이터를 읽어와서 리스트에 저장합니다.
with open(file_path, "r", encoding="utf-8") as f:
    road_names = list(set(f.read().splitlines()))

# 데이터 전처리: 공백 숫자 제거
road_names = [addr.strip().replace(' ', '') for addr in road_names]

# 2. 각 문자를 인덱스로 변환하는 Tokenizer 설정 (Character-level로 설정, 숫자 포함)
tokenizer = Tokenizer(char_level=True, filters="")  # filters를 빈 문자열로 설정하여 숫자 포함
tokenizer.fit_on_texts(road_names)
road_sequences = tokenizer.texts_to_sequences(road_names)

max_len = max(len(seq) for seq in road_sequences)
road_padded = pad_sequences(road_sequences, maxlen=max_len, padding='post')

# 3. 모델 정의
vocab_size = len(tokenizer.word_index) + 1  # 전체 문자 수
embedding_dim = 32  # 문자 임베딩 차원
lstm_units = 64  # LSTM 유닛 수

# 입력 레이어
input_text = Input(shape=(max_len,))
# 문자 임베딩
embedding = Embedding(input_dim=vocab_size, output_dim=embedding_dim, input_length=max_len)(input_text)
# LSTM 레이어로 시퀀스 처리
lstm_out = LSTM(lstm_units)(embedding)
# 벡터로 변환
dense = Dense(embedding_dim, activation='linear')(lstm_out)

# 모델 생성
model = tf.keras.Model(inputs=input_text, outputs=dense)
model.compile(optimizer='adam', loss='mse')

# 4. 모델을 학습 없이 벡터화에만 사용합니다.
road_vectors = model.predict(road_padded)

# 모델 저장 경로
model_save_path = 'C:/Users/all4land/Desktop/road_similarity_model_keras.h5'

# 모델 저장
model.save(model_save_path)
print(f"모델이 '{model_save_path}'에 저장되었습니다.")

# 모델 불러오기
loaded_model = tf.keras.models.load_model(model_save_path)
print("저장된 모델이 성공적으로 불러와졌습니다.")

# 5. 입력 도로명에 대한 유사도 검색 함수
def find_similar_roads(input_road, top_k=400):
    input_seq = tokenizer.texts_to_sequences([input_road])
    input_padded = pad_sequences(input_seq, maxlen=max_len, padding='post')
    input_vector = loaded_model.predict(input_padded)  # 저장된 모델 사용

    # 코사인 유사도를 계산합니다.
    similarities = cosine_similarity(input_vector, road_vectors)[0]
    similar_indices = np.argsort(similarities)[-top_k:][::-1]
    similar_roads = [road_names[i] for i in similar_indices]

    # 중복 제거
    unique_roads = list(dict.fromkeys(similar_roads))
    return unique_roads[:top_k]

# 리벤슈타인 거리로 정렬하는 함수
def sort_addresses_by_levenshtein(input_address, address_list):
    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses

# 테스트
input_data = sys.argv
input_keyword = sys.argv[1]
default_keyword = sys.argv[2] if len(sys.argv[2]) > 0 else '노윈로28길'
analysis_keyword = input_keyword if len(input_keyword) > 0 else default_keyword

# 6. 예시 입력과 유사한 도로명 찾기
top_similar_addresses = find_similar_roads(analysis_keyword, top_k=400)
print(f"입력값 '{analysis_keyword}'와 유사한 도로명:")
for rank, road in enumerate(top_similar_addresses[:400], start=1):
    print(f'{rank}. {road}')

# 리벤슈타인 거리로 정렬된 n개의 유사한 주소
sorted_addresses = sort_addresses_by_levenshtein(analysis_keyword, top_similar_addresses)
print(f'\n입력 "{analysis_keyword}"에 대한 리벤슈타인 거리로 재정렬 된 유사한 주소:')
for rank, addr in enumerate(sorted_addresses[:400], start=1):
    distance = Levenshtein.distance(analysis_keyword, addr)
    print(f'{rank}. {addr} (리벤슈타인 거리: {distance})')

# 결과를 JSON 형식으로 호출서버에 전달
result = {
    "top_similar_addresses": [addr for addr in top_similar_addresses[:400]],
    "sorted_addresses": [addr for addr in sorted_addresses[:400]]
}

# JSON 형식으로 출력
print(json.dumps(result, ensure_ascii=False))