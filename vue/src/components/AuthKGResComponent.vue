<template>
    <div class="result-container">
        <h1>본인인증 결과</h1>
        <div v-if="query.res_cd === '0000'">
            <p>✅ 인증 성공</p>
            <p>이름: {{ query.userName }}</p>
            <p>생년월일: {{ query.userBirth }}</p>
            <p>전화번호: {{ query.userPhone }}</p>
            <p>거래 ID: {{ query.mTxId }}</p>
        </div>
        <div v-else>
            <p>❌ 인증 실패</p>
            <p>오류코드: {{ query.res_cd }}</p>
            <p>오류메시지: {{ query.res_msg }}</p>
        </div>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router';

const route = useRoute();
const query = route.query;
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
