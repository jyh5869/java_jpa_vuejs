import os  # os 모듈 import

import numpy as np
import pandas as pd

import Levenshtein
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# 이 소스 분석하고 기반으로 도로명주소 도출 코드만들어보자.

# 주소 데이터 예시 (30만 개 중 일부만 예시로 사용 테스트를 위함)
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
    # ... 30만 개 데이터
]



# 도로명 주소 텍스트 파일 불러오기
# 도로명 주소 텍스트 파일 불러오기
def load_addresses_from_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        addresses = [line.strip() for line in file.readlines()]
    return list(set(addresses))

# 텍스트 파일 경로
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'  # 파일 경로를 적절히 수정하세요

# 주소 데이터 로드
addresses = load_addresses_from_file(file_path)


# TF-IDF 벡터화 
# 1. char -> 문자 단위로 백터화(default는 단어 단위로 백터화) 
# 2. ngram_range 1,3 -> 단어를 1~3개로 쪼개어 유사도 분석 
vectorizer = TfidfVectorizer(analyzer='char', ngram_range=(1, 3))
tfidf_matrix = vectorizer.fit_transform(addresses)

# 벡터화 객체와 매트릭스를 저장하는 함수
def save_vectorizer_and_matrix(vectorizer, tfidf_matrix, vectorizer_path, matrix_path, addresses_path):
    joblib.dump(vectorizer, vectorizer_path)
    joblib.dump(tfidf_matrix, matrix_path)
    joblib.dump(addresses, addresses_path)

# 데이터 경로 설정
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'
vectorizer_path = 'C:/Users/all4land/Desktop/tfidf_vectorizer.pkl'
matrix_path = 'C:/Users/all4land/Desktop/tfidf_matrix.pkl'
addresses_path = 'C:/Users/all4land/Desktop/addresses_list.pkl'

save_vectorizer_and_matrix(vectorizer, tfidf_matrix, vectorizer_path, matrix_path, addresses_path)

def find_most_similar_address(input_address, addresses, tfidf_matrix):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    most_similar_index = np.argmax(cosine_similarities)
    return addresses[most_similar_index], cosine_similarities[most_similar_index]

def find_top_n_similar_addresses(input_address, addresses, tfidf_matrix, top_n=300):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    top_indices = cosine_similarities.argsort()[-top_n:][::-1]
    return [(addresses[i], cosine_similarities[i]) for i in top_indices]

def sort_addresses_by_levenshtein(input_address, address_list):
    # 리벤슈타인 거리를 기준으로 정렬
    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses

# 도로명에서 "로", "대로", "길", "도로" 등을 제거하는 함수
def preprocess_address(address):
    # 패턴 정의: '로', '대로', '길', '도로' 뒤의 모든 부분을 제거
    pattern = r'(로|대로|길|도로).*'
    return re.sub(pattern, '', address)
    
# 테스트
test_address = "노윈로"
predicted_address, similarity = find_most_similar_address(test_address, addresses, tfidf_matrix)
print(f'입력된  "{test_address}"에 대한 가장 유사한 주소: {predicted_address} (유사도: {similarity*100:.2f}%)')

top_similar_addresses = find_top_n_similar_addresses(test_address, addresses, tfidf_matrix)
print(f'입력된 "{test_address}"에 대한 상위 {len(top_similar_addresses)}개 유사한 주소:')
for rank, (addr, similarity) in enumerate(top_similar_addresses, start=1):
    print(f'{rank}. {addr} (유사도: {similarity*100:.2f}%)')



# 리벤슈타인 거리로 정렬
sorted_addresses = sort_addresses_by_levenshtein(test_address, [addr for addr, _ in top_similar_addresses])
print(f'\n리벤슈타인 거리로 재정렬된 유사한 주소:')
for rank, addr in enumerate(sorted_addresses, start=1):
    distance = Levenshtein.distance(test_address, addr)
    print(f'{rank}. {addr} (리벤슈타인 거리: {distance})')