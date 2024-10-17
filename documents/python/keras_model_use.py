import re
import os
import sys

import json

import numpy as np
import pandas as pd

import joblib as joblib
import Levenshtein as Levenshtein

from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, LSTM, Dense


# 주소 데이터
addresses = [
    "안곡로197번길",
    "인곡로191번길",
    "안곡로203번길",
    "안곡로205번길",
    "안곡로266번길",
    "안곡로269번길",
    "안곡로279번길",
    "인곡길280번길",
    "중앙로",
    "증앙로",
    "중잉로",
    "중아ㅇ로",
    "중앙로1길",
    "중앙로2길",
    "중앙로3길",
    "통도9길",
    "편들1길",
    "평산10길",
    "평산11길",
    "평산12길",
    "낙영3길"
]

# 도로명 주소 텍스트 파일 불러오기
def load_addresses_from_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        addresses = [line.strip() for line in file.readlines()]
    return list(set(addresses))

# 텍스트 파일 경로
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'  # 파일 경로를 적절히 수정하세요

# 주소 데이터 로드
# addresses = load_addresses_from_file(file_path)


# 데이터 전처리: 공백 및 숫자 제거
# addresses_processed = [re.sub(r'\d+', '', addr).strip().replace(' ', '') for addr in addresses]

# 데이터 전처리: 공백만 제거하고 숫자는 그대로 유지
addresses_processed = [addr.strip().replace(' ', '') for addr in addresses]

# Tokenizer를 사용하여 텍스트를 시퀀스로 변환
tokenizer = Tokenizer()
tokenizer.fit_on_texts(addresses_processed)
sequences = tokenizer.texts_to_sequences(addresses_processed)

# 시퀀스 데이터로 변환 및 패딩
max_length = max(len(seq) for seq in sequences)
padded_sequences = pad_sequences(sequences, maxlen=max_length)

# 모델 구성
model = Sequential()
model.add(Embedding(input_dim=len(tokenizer.word_index) + 1, output_dim=32, input_length=max_length))
model.add(LSTM(64))
model.add(Dense(len(tokenizer.word_index), activation='softmax'))

model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])

# 모델 요약 정보 출력
print(model.summary())

# 모델 훈련
try:
    num_epochs = 2
    model.fit(padded_sequences, np.array(sequences), epochs=num_epochs, verbose=1)
    print("모델 훈련이 완료되었습니다.")
except Exception as e:
    print(f"Error during training: {str(e)}")
    sys.exit(1)

# 모델 평가 (여기서는 테스트 데이터가 없으므로 훈련 데이터에 대한 성능 평가만 가능)
loss, accuracy = model.evaluate(padded_sequences, np.array(sequences), verbose=1)
print(f'Accuracy: {accuracy*100:.2f}%')

# 예측 함수 수정
def predict_similar_address(input_address):
    input_processed = input_address.strip().replace(' ', '')
    input_sequence = tokenizer.texts_to_sequences([input_processed])
    padded_input_sequence = pad_sequences(input_sequence, maxlen=max_length)
    predicted_probs = model.predict(padded_input_sequence)[0]
    print("11111")
    # 후처리: 입력 주소가 포함된 주소들이 높은 우선순위로 정렬
    predicted_indices = np.argsort(predicted_probs)[::-1]
    print(predicted_probs)
    for idx in predicted_indices:
        predicted_address = addresses[idx]
        if input_address in predicted_address:
            return predicted_address
    print("11111")
    # 입력 주소가 포함된 주소가 없을 경우, 가장 높은 확률의 주소 반환
    predicted_index = predicted_indices[0]
    predicted_address = addresses[predicted_index]
    return predicted_address

def predict_top_similar_addresses(input_address, top_n=10):
    input_processed = input_address.strip().replace(' ', '')
    input_sequence = tokenizer.texts_to_sequences([input_processed])
    padded_input_sequence = pad_sequences(input_sequence, maxlen=max_length)
    
    # 모델 예측
    predicted_probs = model.predict(padded_input_sequence)[0]
    
    # 상위 N개의 주소 예측
    top_predicted_indices = np.argsort(predicted_probs)[::-1][:top_n]
    top_similar_addresses = [tokenizer.index_word[idx] for idx in top_predicted_indices]
    
    return top_similar_addresses

# 리벤슈타인 거리로 정렬하는 함수
def sort_addresses_by_levenshtein(input_address, address_list):

    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses


# 테스트
input_data = sys.argv
input_keyword = sys.argv[1]
default_keyword = sys.argv[2] if len(sys.argv[2]) > 0 else '노윈로'
analysis_keyword = input_keyword if len(input_keyword) > 0 else default_keyword

predicted_address = predict_similar_address(analysis_keyword)
print(f'입력 "{analysis_keyword}"에 대한 예측 결과: {predicted_address}')

top_similar_addresses = predict_top_similar_addresses(analysis_keyword)
print(f'입력 "{analysis_keyword}"에 대한 상위 5개 예측 결과:')
for rank, addr in enumerate(top_similar_addresses, start=1):
    print(f'{rank}. {addr}')

# 리벤슈타인 거리로 정렬된 n개의 유사한 주소
sorted_addresses = sort_addresses_by_levenshtein(analysis_keyword,  top_similar_addresses)
print(f'\n입력 "{analysis_keyword}"에 대한 리벤슈타인 거리로 재정렬 된 유사한 주소:')
for rank, addr in enumerate(sorted_addresses[:200], start=1):
    distance = Levenshtein.distance(analysis_keyword, addr)
    print(f'{rank}. {addr} (리벤슈타인 거리: {distance})')


# 결과를 JSON 형식으로 호출서버에 전달
result = {
    "top_similar_addresses": [addr for addr in top_similar_addresses[:200]],
    "sorted_addresses": [addr for addr in sorted_addresses[:200]]
}

# JSON 형식으로 출력
print(json.dumps(result, ensure_ascii=False))