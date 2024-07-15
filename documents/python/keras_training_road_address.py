import numpy as np
import pandas as pd
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, LSTM, Dense
import sys
import re

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
    "중앙로1길",
    "중앙로2길",
    "중앙로3길"
]

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
model.add(Dense(len(tokenizer.word_index) + 1, activation='softmax'))

model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])

# 모델 요약 정보 출력
print(model.summary())

# 모델 훈련
try:
    num_epochs = 20
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
    
    # 후처리: 입력 주소가 포함된 주소들이 높은 우선순위로 정렬
    predicted_indices = np.argsort(predicted_probs)[::-1]
    for idx in predicted_indices:
        predicted_address = tokenizer.index_word[idx]
        if input_address in predicted_address:
            return predicted_address
    
    # 입력 주소가 포함된 주소가 없을 경우, 가장 높은 확률의 주소 반환
    predicted_index = predicted_indices[0]
    predicted_address = tokenizer.index_word[predicted_index]
    return predicted_address




def predict_top_similar_addresses(input_address, top_n=10):
    input_processed = input_address.strip().replace(' ', '')
    input_sequence = tokenizer.texts_to_sequences([input_processed])
    padded_input_sequence = pad_sequences(input_sequence, maxlen=max_length)
    
    # 모델 예측
    predicted_probs = model.predict(padded_input_sequence)[0]
    
    # 상위 N개의 주소 예측
    top_predicted_indices = np.argsort(predicted_probs)[::-1][:top_n]
    top_predicted_addresses = [tokenizer.index_word[idx] for idx in top_predicted_indices]
    
    return top_predicted_addresses


# 테스트
test_address = "중앙로"

predicted_address = predict_similar_address(test_address)
print(f'입력 "{test_address}"에 대한 예측 결과: {predicted_address}')

top_predicted_addresses = predict_top_similar_addresses(test_address)
print(f'입력 "{test_address}"에 대한 상위 5개 예측 결과:')
for rank, addr in enumerate(top_predicted_addresses, start=1):
    print(f'{rank}. {addr}')