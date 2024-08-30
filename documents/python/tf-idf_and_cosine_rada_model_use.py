import os  # os 모듈 import

import numpy as np
import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import Levenshtein

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

# 유사한 주소를 찾는 함수
def find_most_similar_address(input_address, addresses, vectorizer, tfidf_matrix):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    most_similar_index = np.argmax(cosine_similarities)
    print(f'유사한 코싸인 ======  {cosine_similarities}')
    print(f'유사한 인덱스 ======  {most_similar_index}')
    return addresses[most_similar_index], cosine_similarities[most_similar_index]

def find_top_n_similar_addresses(input_address, addresses, tfidf_matrix, top_n=200):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    top_indices = cosine_similarities.argsort()[-top_n:][::-1]
    print(f'유사한 코싸인들 ======  {cosine_similarities[0]}')
    print(f'유사한 인덱스들 ======  {top_indices}')
    return [(addresses[i], cosine_similarities[i]) for i in top_indices]

# 리벤슈타인 거리로 정렬하는 함수
def sort_addresses_by_levenshtein(input_address, address_list):
    # 리벤슈타인 거리를 기준으로 정렬
    sorted_addresses = sorted(address_list, key=lambda x: Levenshtein.distance(input_address, x))
    return sorted_addresses


# 데이터 경로 설정
file_path = 'C:/Users/all4land/Desktop/java_jpa_vuejs/documents/leaningData/addrKorRoadName.txt'
vectorizer_path = 'C:/Users/all4land/Desktop/tfidf_vectorizer.pkl'
matrix_path = 'C:/Users/all4land/Desktop/tfidf_matrix.pkl'
addresses_path = 'C:/Users/all4land/Desktop/addresses_list.pkl'



def makeDump():

    addresses = load_addresses_from_file(file_path)
    # 첫 번째 실행: 벡터화 수행 및 저장
    vectorizer = TfidfVectorizer(analyzer='char', ngram_range=(1, 3))
    tfidf_matrix = vectorizer.fit_transform(addresses)
    save_vectorizer_and_matrix(vectorizer, tfidf_matrix, vectorizer_path, matrix_path, addresses_path, addresses)
    # 이후 실행: 저장된 벡터화 객체와 매트릭스 불러오기



# 파일 존재 여부 체크
if not os.path.isfile(matrix_path):
    # 파일이 존재할 경우 실행할 코드
    file_name = os.path.basename(matrix_path)
    print(f'파일 "{file_name}"이(가) 경로 "{matrix_path}"에 존재하지 않습니다.')

    makeDump()
    # 여기에 파일이 존재할 때 실행할 작업을 추가하세요


vectorizer, tfidf_matrix, addresses = load_vectorizer_and_matrix(vectorizer_path, matrix_path, addresses_path)

# 테스트
test_address = "중잉로"
predicted_address, similarity = find_most_similar_address(test_address, addresses, vectorizer, tfidf_matrix)
print(f'입력된 "{test_address}"에 대한 가장 유사한 주소: {predicted_address} (유사도: {similarity*100:.2f}%)')

top_similar_addresses = find_top_n_similar_addresses(test_address, addresses, tfidf_matrix)
print(f'입력된 "{test_address}"에 대한 상위 {len(top_similar_addresses)}개 유사한 주소:')
for rank, (addr, similarity) in enumerate(top_similar_addresses, start=1):
    print(f'{rank}. {addr} (유사도: {similarity*100:.2f}%)')

# 리벤슈타인 거리로 정렬된 유사한 주소
sorted_addresses = sort_addresses_by_levenshtein(test_address, [addr for addr, _ in top_similar_addresses])
print(f'\n리벤슈타인 거리로 재정렬된 유사한 주소:')
for rank, addr in enumerate(sorted_addresses[:200], start=1):
    distance = Levenshtein.distance(test_address, addr)
    print(f'{rank}. {addr} (리벤슈타인 거리: {distance})')