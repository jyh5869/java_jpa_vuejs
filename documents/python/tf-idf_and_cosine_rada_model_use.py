import numpy as np
import pandas as pd

import Levenshtein
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# 도로명 주소 텍스트 파일 불러오기
def load_addresses_from_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        addresses = [line.strip() for line in file.readlines()]

    # 중복 제거
    addresses = list(set(addresses))

    return addresses

# 텍스트 파일 경로
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'  # 파일 경로를 적절히 수정하세요

# 주소 데이터 로드
addresses = load_addresses_from_file(file_path)


# TF-IDF 벡터화 
# 1. char -> 문자 단위로 백터화(default는 단어 단위로 백터화) 
# 2. ngram_range 1,3 -> 단어를 1~3개로 쪼개어 유사도 분석 

# TfidfVectorizer 객체 불러오기
vectorizer  = joblib.load('C:/Users/all4land/Desktop/tfidf_vectorizer.pkl')

# TF-IDF 매트릭스 불러오기 (필요한 경우에만 불러오기)
tfidf_matrix  = joblib.load('C:/Users/all4land/Desktop/tfidf_matrix.pkl')

# 유사한 주소를 찾는 함수
def find_most_similar_address(input_address, addresses, tfidf_matrix):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    most_similar_index = np.argmax(cosine_similarities)
    return addresses[most_similar_index], cosine_similarities[most_similar_index]

# 상위 n개의 유사한 주소를 찾는 함수
def find_top_n_similar_addresses(input_address, addresses, tfidf_matrix, top_n=300):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    top_indices = cosine_similarities.argsort()[-top_n:][::-1]
    return [(addresses[i], cosine_similarities[i]) for i in top_indices]

# 리벤슈타인 거리로 정렬하는 함수
def sort_addresses_by_levenshtein(input_address, address_list):
    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses

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