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

tokenizer = Tokenizer(char_level=True, filters="")  # filters를 빈 문자열로 설정하여 숫자 포함

# 모델 저장 경로
model_save_path = 'C:/Users/all4land/Desktop/road_similarity_model_keras.h5'
model_config_save_path =  'C:/Users/all4land/Desktop/road_similarity_model_keras_conf.json'
model_vectors_save_path =  'C:/Users/all4land/Desktop/road_similarity_model_keras_vectors.npy'
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt' 

# 1. 도로명 데이터를 읽어와서 리스트에 저장합니다.
with open(file_path, "r", encoding="utf-8") as f:
    road_names = list(set(f.read().splitlines()))

# 모델 호출 시 max_len 불러오기
with open(model_config_save_path, 'r') as f:
    max_len = json.load(f)['max_len']

# 메돌 호출 시 vertor데이터 불러오기
road_vectors = np.load(model_vectors_save_path)

# 모델 불러오기
loaded_model = tf.keras.models.load_model(model_save_path)
print('저장된 모델이 성공적으로 불러와졌습니다.')

# 5. 입력 도로명에 대한 유사도 검색 함수
def find_similar_roads(input_road, top_k=400):

    input_seq = tokenizer.texts_to_sequences([input_road])
    input_padded = pad_sequences(input_seq, maxlen=max_len, padding='post')
    input_vector = loaded_model.predict(input_padded)  # 저장된 모델 사용
    print('1111111')
    print(input_vector)

      # 입력 도로명의 벡터와 전체 도로명 벡터들 간의 코사인 유사도 계산
    similarities = cosine_similarity(input_vector, road_vectors)[0]
    
    # 입력 도로명에 가장 유사한 도로명 찾기
    most_similar_index = np.argmax(similarities)
    most_similar_road_name = road_names[most_similar_index]
    most_similar_score = similarities[most_similar_index]

    print(f"'{input_road}'의 벡터는 '{most_similar_road_name}'와 가장 유사합니다 (유사도: {most_similar_score:.4f})")

    # 코사인 유사도를 계산합니다.
    similarities = cosine_similarity(input_vector, road_vectors)[0]
    similar_indices = np.argsort(similarities)[-top_k:][::-1]
    similar_roads = [road_names[i] for i in similar_indices]
    print('22222.')
    
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
for rank, road in enumerate(top_similar_addresses[:100], start=1):
    print(f'{rank}. {road}')

# 리벤슈타인 거리로 정렬된 n개의 유사한 주소
sorted_addresses = sort_addresses_by_levenshtein(analysis_keyword, top_similar_addresses)
print(f'\n입력 "{analysis_keyword}"에 대한 리벤슈타인 거리로 재정렬 된 유사한 주소:')
for rank, addr in enumerate(sorted_addresses[:100], start=1):
    distance = Levenshtein.distance(analysis_keyword, addr)
    print(f'{rank}. {addr} (리벤슈타인 거리: {distance})')

# 결과를 JSON 형식으로 호출서버에 전달
result = {
    "top_similar_addresses": [addr for addr in top_similar_addresses[:100]],
    "sorted_addresses": [addr for addr in sorted_addresses[:100]]
}

# JSON 형식으로 출력
print(json.dumps(result, ensure_ascii=False))