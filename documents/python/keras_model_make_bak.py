import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, LSTM, Dense
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.metrics.pairwise import cosine_similarity
import Levenshtein as Levenshtein

# 데이터 준비
# road_names = ['성암로', '노원로', '노윈로', '가락로', '대청로', '양재로', '누원로', '중앙로']

# 주소 데이터
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'  # 파일 경로를 적절히 수정하세요

# 1. 도로명 데이터를 읽어와서 리스트에 저장합니다.
with open(file_path, "r", encoding="utf-8") as f:
    road_names = list(set(f.read().splitlines()))

# 데이터 전처리: 공백 숫자 제거
road_names = [addr.strip().replace(' ', '') for addr in road_names]

# 1. Tokenizer와 패딩
tokenizer = Tokenizer(char_level=True)
tokenizer.fit_on_texts(road_names)
sequences = tokenizer.texts_to_sequences(road_names)
max_len = max(len(seq) for seq in sequences)
padded_sequences = pad_sequences(sequences, maxlen=max_len)

# 2. 모델 설계
model = Sequential()
model.add(Embedding(input_dim=len(tokenizer.word_index)+1, output_dim=32, input_length=max_len))
model.add(LSTM(64))
model.add(Dense(32, activation='linear'))

# 모델 컴파일
model.compile(optimizer='adam', loss='mse')

# 3. 모델 학습
try:
    model.fit(padded_sequences, padded_sequences, epochs=3, verbose=1)  # epochs를 10으로 설정
except Exception as e:
    print(f"Error during model training: {e}")

# 4. 유사한 도로명 찾기
def find_similar_road(input_road):
    input_seq = tokenizer.texts_to_sequences([input_road])
    input_padded = pad_sequences(input_seq, maxlen=max_len)
    input_vector = model.predict(input_padded)

    # 모든 도로명 벡터를 가져오기
    road_vectors = model.predict(padded_sequences)

    # 코사인 유사도 계산
    similarities = cosine_similarity(input_vector, road_vectors)[0]
    similar_indices = np.argsort(similarities)[-500:][::-1]  # 상위 3개 추출

    return [road_names[i] for i in similar_indices]


# 리벤슈타인 거리로 정렬하는 함수
def sort_addresses_by_levenshtein(input_address, address_list):
    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses


# 테스트
analysis_keyword = '노윈로'
top_similar_addresses = find_similar_road(analysis_keyword)
print(f"입력 '{analysis_keyword}'에 대한 유사한 도로명: {top_similar_addresses}")

# 리벤슈타인 거리로 정렬된 n개의 유사한 주소
sorted_addresses = sort_addresses_by_levenshtein(analysis_keyword, top_similar_addresses)
print(f'\n입력 "{analysis_keyword}"에 대한 리벤슈타인 거리로 재정렬 된 유사한 주소:')
for rank, addr in enumerate(sorted_addresses[:400], start=1):
    distance = Levenshtein.distance(analysis_keyword, addr)
    print(f'{rank}. {addr} (리벤슈타인 거리: {distance})')
