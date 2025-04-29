<template>
    <div class="result-container">
        <h1>본인인증 결과</h1>

        <!-- 성공/실패 여부 -->
        <div>
            <p v-if="query.resultCode === '0000'">✅ 인증 성공</p>
            <p v-else>❌ 인증 실패</p>
        </div>
        {{ query }}
        <!-- 모든 파라미터를 테이블 형식으로 출력 -->
        <table>
            <tr>
                <th>결과코드</th>
                <td>{{ query.resultCode }}</td>
            </tr>
            <tr>
                <th>결과메시지</th>
                <td>{{ decode(query.resultMsg) }}</td>
            </tr>
            <tr v-if="query.txId">
                <th>통합인증 트랜잭션 ID (txId)</th>
                <td>{{ query.txId }}</td>
            </tr>
            <tr v-if="query.mTxId">
                <th>가맹점 트랜잭션 ID (mTxId)</th>
                <td>{{ query.mTxId }}</td>
            </tr>
            <tr v-if="query.svcCd">
                <th>요청구분코드 (svcCd)</th>
                <td>{{ query.svcCd }}</td>
            </tr>
            <tr v-if="query.providerDevCd">
                <th>제휴사코드 (providerDevCd)</th>
                <td>{{ query.providerDevCd }}</td>
            </tr>
            <tr v-if="query.userName">
                <th>사용자 이름 (userName)</th>
                <td>{{ decode(query.userName) }}</td>
            </tr>
            <tr v-if="query.userPhone">
                <th>사용자 전화번호 (userPhone)</th>
                <td>{{ decode(query.userPhone) }}</td>
            </tr>
            <tr v-if="query.userBirth">
                <th>사용자 생년월일 (userBirth)</th>
                <td>{{ decode(query.userBirth) }}</td>
            </tr>
            <tr v-if="query.userBirthday">
                <th>사용자 생년월일 (userBirthday)</th>
                <td>{{ decode(query.userBirthday) }}</td>
            </tr>
            <tr v-if="query.userCi">
                <th>CI 데이터 (userCi)</th>
                <td>{{ decode(query.userCi) }}</td>
            </tr>
            <tr v-if="query.signedData">
                <th>서명데이터 (signedData)</th>
                <td>{{ decode(query.signedData) }}</td>
            </tr>
            <tr v-if="query.userGender">
                <th>사용자 성별 (userGender)</th>
                <td>{{ decode(query.userGender) }}</td>
            </tr>
            <tr v-if="query.isForeign">
                <th>내외국인 정보 (isForeign)</th>
                <td>{{ decode(query.isForeign) }}</td>
            </tr>
            <tr v-if="query.userDi">
                <th>DI 데이터 (userDi)</th>
                <td>{{ decode(query.userDi) }}</td>
            </tr>
            <tr v-if="query.userCi2">
                <th>CI2 데이터 (userCi2)</th>
                <td>{{ decode(query.userCi2) }}</td>
            </tr>
            <tr v-if="query.token">
                <th>토큰 (token)</th>
                <td>{{ decode(query.token) }}</td>
            </tr>
            <tr v-if="query.authRequestUrl">
                <th>인증 요청 URL (authRequestUrl)</th>
                <td>{{ query.authRequestUrl }}</td>
            </tr>
            <tr v-if="query.redirectTo">
                <th>리다이렉트 (redirectTo)</th>
                <td>{{ query.redirectTo }}</td>
            </tr>
        </table>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router';

const route = useRoute();
const query = route.query;

/**
 * URL 인코딩된 값을 디코딩합니다.
 * 디코딩 실패 시 원본 값을 그대로 반환합니다.
 */
function decode(val) {
    try {
        return decodeURIComponent(val);
    } catch {
        return val;
    }
}
</script>

<style scoped>
.result-container {
    margin: 50px auto;
    max-width: 600px;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #fafafa;
}
h1 {
    text-align: center;
    margin-bottom: 20px;
}
table {
    width: 100%;
    border-collapse: collapse;
}
th,
td {
    padding: 8px;
    border-bottom: 1px solid #ddd;
    text-align: left;
}
th {
    width: 200px;
    background-color: #f0f0f0;
}
p {
    margin: 10px 0;
}
</style>
