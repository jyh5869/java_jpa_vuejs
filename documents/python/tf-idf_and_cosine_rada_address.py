import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# 이 소스 분석하고 기반으로 도로명주소 도출 코드만들어보자.

# 주소 데이터 예시 (30만 개 중 일부만 예시로 사용)
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

# TF-IDF 벡터화
vectorizer = TfidfVectorizer()
tfidf_matrix = vectorizer.fit_transform(addresses)

def find_most_similar_address(input_address, addresses, tfidf_matrix):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    most_similar_index = np.argmax(cosine_similarities)
    return addresses[most_similar_index], cosine_similarities[most_similar_index]

def find_top_n_similar_addresses(input_address, addresses, tfidf_matrix, top_n=5):
    input_vec = vectorizer.transform([input_address])
    cosine_similarities = cosine_similarity(input_vec, tfidf_matrix).flatten()
    top_indices = cosine_similarities.argsort()[-top_n:][::-1]
    return [(addresses[i], cosine_similarities[i]) for i in top_indices]

# 테스트
test_address = "증앙로"
predicted_address, similarity = find_most_similar_address(test_address, addresses, tfidf_matrix)
print(f'입력 "{test_address}"에 대한 가장 유사한 주소: {predicted_address} (유사도: {similarity*100:.2f}%)')

top_similar_addresses = find_top_n_similar_addresses(test_address, addresses, tfidf_matrix)
print(f'입력 "{test_address}"에 대한 상위 {len(top_similar_addresses)}개 유사한 주소:')
for rank, (addr, similarity) in enumerate(top_similar_addresses, start=1):
    print(f'{rank}. {addr} (유사도: {similarity*100:.2f}%)')