import os  
import sys
import json

import numpy as np
import joblib as joblib
import Levenshtein as Levenshtein

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


# 도로명 주소 텍스트 파일 불러오기
def load_addresses_from_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        addresses = [line.strip() for line in file.readlines()]
    return list(set(addresses))

# 벡터화 객체와 매트릭스를 저장하는 함수
def save_vectorizer_and_matrix(vectorizer, tfidf_matrix, vectorizer_path, matrix_path, addresses_path, addresses):
    joblib.dump(vectorizer, vectorizer_path)
    joblib.dump(tfidf_matrix, matrix_path)
    joblib.dump(addresses, addresses_path)

# 벡터화 객체와 매트릭스를 불러오는 함수
def load_vectorizer_and_matrix(vectorizer_path, matrix_path, addresses_path):
    vectorizer = joblib.load(vectorizer_path)
    tfidf_matrix = joblib.load(matrix_path)
    addresses = joblib.load(addresses_path)
    return vectorizer, tfidf_matrix, addresses

# 유사한 주소를 찾는 함수 TOP1
def find_most_similar_address(input_address, addresses, vectorizer, tfidf_matrix):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    most_similar_index = np.argmax(cosine_similarities)

    print(f'유사 코싸인 TOP1 - {cosine_similarities}')
    print(f'유사 인덱스 TOP1 - {most_similar_index}')
    
    return addresses[most_similar_index], cosine_similarities[most_similar_index]

# 유사한 주소를 찾는 함수 TOP200
def find_top_n_similar_addresses(input_address, addresses, tfidf_matrix, top_n=200):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    top_indices = cosine_similarities.argsort()[-top_n:][::-1]
    
    print(f'유사 코싸인 TOP{top_n} - {cosine_similarities}')
    print(f'유사 인덱스 TOP{top_n} - {top_indices}')
    
    return [(addresses[i], cosine_similarities[i]) for i in top_indices]

# 리벤슈타인 거리로 정렬하는 함수
def sort_addresses_by_levenshtein(input_address, address_list):
    
    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses

#  주소데이터를 백터화 한 후 저장하는 함수
def make_address_dump():
    print(f'CALL - 주소데이터 백터화 모델 생성')
    addresses = load_addresses_from_file(file_path)
    vectorizer = TfidfVectorizer(analyzer='char', ngram_range=(1, 3))
    tfidf_matrix = vectorizer.fit_transform(addresses)

    save_vectorizer_and_matrix(vectorizer, tfidf_matrix, vectorizer_path, matrix_path, addresses_path, addresses)


# 백터화 한 분석 모듈의 저장 경로 정의
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'
vectorizer_path = 'C:/Users/all4land/Desktop/tfidf_vectorizer.pkl'
matrix_path = 'C:/Users/all4land/Desktop/tfidf_matrix.pkl'
addresses_path = 'C:/Users/all4land/Desktop/addresses_list.pkl'

file_paths_arr = [file_path, vectorizer_path, matrix_path, addresses_path]

# 벡터화 한 분석 모듈의 존재 여부 체크 후 없으면 생성
for file_path in file_paths_arr:
    file_name = os.path.basename(file_path)

    if os.path.isfile(file_path):
        print(f'파일 "{file_name}"이(가) 경로 "{matrix_path}"에 존재합니다.')
    else:
        print(f'파일 "{file_name}"이(가) 경로 "{matrix_path}"에 존재하지 않습니다.')
        make_address_dump()
        break
    
# 주소데이터 분석모듈 호출
vectorizer, tfidf_matrix, addresses = load_vectorizer_and_matrix(vectorizer_path, matrix_path, addresses_path)


# 모듈 테스트 및 분석
input_data = sys.argv
input_keyword = sys.argv[1]
default_keyword = sys.argv[2] if len(sys.argv[2]) > 0 else '노윈로'
analysis_keyword = input_keyword if len(input_keyword) > 0 else default_keyword

# 가장 유사한 하나의 주소 
predicted_address, similarity = find_most_similar_address(analysis_keyword, addresses, vectorizer, tfidf_matrix)
print(f'입력된 "{analysis_keyword}"에 대한 가장 유사한 주소: {predicted_address} (유사도: {similarity*100:.2f}%)')

# 가장 유사한 n개의 주소
top_similar_addresses = find_top_n_similar_addresses(analysis_keyword, addresses, tfidf_matrix)
print(f'입력된 "{analysis_keyword}"에 대한 상위 {len(top_similar_addresses)}개 유사한 주소:')
for rank, (addr, similarity) in enumerate(top_similar_addresses, start=1):
    print(f'{rank}. {addr} (유사도: {similarity*100:.2f}%)')

# 리벤슈타인 거리로 정렬된 n개의 유사한 주소
sorted_addresses = sort_addresses_by_levenshtein(analysis_keyword, [addr for addr, _ in top_similar_addresses])
print(f'\n리벤슈타인 거리로 재정렬된 유사한 주소:')
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
