<template>
    <div class="result-container">
        <h1>본인인증 결과</h1>

        <!-- resultCode 가 "0000" 이면 성공 화면 -->
        <div v-if="query.resultCode === '0000'">
            <p>✅ 인증 성공</p>
            <!-- 인증 요청에 사용된 URL -->
            <p>인증 요청 URL: {{ query.authRequestUrl }}</p>
            <!-- 사용자 정보 (URL 인코딩된 값이 올 수 있으므로 decode) -->
            <p>이름: {{ decode(query.userName) }}</p>
            <p>생년월일: {{ decode(query.userBirth) }}</p>
            <p>전화번호: {{ decode(query.userPhone) }}</p>
            <!-- 거래 ID -->
            <p>거래 ID: {{ query.txId }}</p>
            <!-- 발급된 토큰 -->
            <p>토큰: {{ query.token }}</p>
            <!-- Inicis 결과 메시지 (예: 성공/실패 사유) -->
            <p>결과 메시지: {{ decode(query.resultMsg) }}</p>
        </div>

        <!-- resultCode 가 "0000"이 아니면 실패 화면 -->
        <div v-else>
            <p>❌ 인증 실패</p>
            <p>오류 코드: {{ query.resultCode }}</p>
            <p>오류 메시지: {{ decode(query.resultMsg) }}</p>
            <!-- (원한다면 아래 항목들도 실패 시 표시할 수 있습니다) -->
            <!--
        <p>인증 요청 URL: {{ query.authRequestUrl }}</p>
        <p>거래 ID: {{ query.txId }}</p>
        <p>토큰: {{ query.token }}</p>
        --></div>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router';

// useRoute 로 query 파라미터 전체를 가져옵니다.
const route = useRoute();
const query = route.query;

/**
 * URL 인코딩된 데이터를 읽기 쉽게 디코딩해 주는 헬퍼.
 * decodeURIComponent 에 실패하면 원본 값을 그대로 리턴.
 */
function decode(value) {
    try {
        return decodeURIComponent(value);
    } catch {
        return value;
    }
}
</script>

<style scoped>
.result-container {
    margin: 50px auto;
    max-width: 500px;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
    text-align: left;
}
h1 {
    text-align: center;
    margin-bottom: 20px;
}
p {
    margin: 10px 0;
    font-size: 16px;
}
</style>
