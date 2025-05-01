<template>
    <div class="result-container" v-if="isInicis">
        <header class="result-header">
            <h1>본인인증 결과</h1>
            <div class="status" :class="{ success: isSuccess, fail: !isSuccess }">
                <span v-if="isSuccess">✅ 인증 성공</span>
                <span v-else>❌ 인증 실패</span>
            </div>
        </header>
        <div class="table-wrapper">
            <table class="result-table">
                <tbody>
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
                    <tr v-if="query.userBirthday">
                        <th>사용자 생년월일 (userBirthday)</th>
                        <td>{{ decode(query.userBirthday) }}</td>
                    </tr>
                    <tr v-if="query.userCi">
                        <th>CI 데이터 (userCi)</th>
                        <td class="mono">{{ decode(query.userCi) }}</td>
                    </tr>
                    <tr v-if="query.signedData">
                        <th>서명데이터 (signedData)</th>
                        <td class="mono">{{ decode(query.signedData) }}</td>
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
                        <td class="mono">{{ decode(query.userDi) }}</td>
                    </tr>
                    <tr v-if="query.userCi2">
                        <th>CI2 데이터 (userCi2)</th>
                        <td class="mono">{{ decode(query.userCi2) }}</td>
                    </tr>
                    <tr v-if="query.token">
                        <th>토큰 (token)</th>
                        <td class="mono">{{ decode(query.token) }}</td>
                    </tr>
                    <tr v-if="query.authRequestUrl">
                        <th>인증 요청 URL</th>
                        <td class="mono">{{ query.authRequestUrl }}</td>
                    </tr>
                    <tr v-if="query.redirectTo">
                        <th>리다이렉트 경로</th>
                        <td>{{ query.redirectTo }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div class="nice-result" v-if="isNice">
        <h2>NICE 본인인증 결과</h2>
        <div v-if="result">
            <div class="result-item" v-for="(value, key) in result" :key="key">
                <span class="label">{{ key }}</span>
                <span class="value">{{ decode(value) }}</span>
            </div>
        </div>
        <div v-else>
            <p>본인인증 결과를 불러올 수 없습니다.</p>
        </div>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
const route = useRoute();
const query = route.query;
const isSuccess = query.resultCode === '0000';

const authType = query.type || (query.txId ? 'INICIS' : 'NICE');
const isNice = authType === 'NICE_AUTH_COMPLETE' || authType === 'NICE';
const isInicis = authType === 'INICIS_AUTH_COMPLETE' || authType === 'INICIS';

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
    max-width: 800px;
    margin: 2rem auto;
    padding: 1rem;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.result-header {
    text-align: center;
    margin-bottom: 1.5rem;
}

.result-header h1 {
    margin: 0;
    font-size: 1.75rem;
    color: #333;
}

.status {
    margin-top: 0.5rem;
    font-size: 1.25rem;
    font-weight: bold;
}
.status.success {
    color: #2e7d32;
}
.status.fail {
    color: #c62828;
}

.table-wrapper {
    overflow-x: auto;
}

.result-table {
    width: 100%;
    border-collapse: collapse;
}
.result-table th,
.result-table td {
    padding: 0.75rem 1rem;
    border-bottom: 1px solid #e0e0e0;
}
.result-table th {
    background: #fafafa;
    color: #555;
    text-align: left;
    white-space: nowrap;
}
.result-table td.mono {
    font-family: monospace;
    word-break: break-all;
}

@media (max-width: 600px) {
    .result-container {
        margin: 1rem;
        padding: 0.75rem;
    }
    .result-header h1 {
        font-size: 1.5rem;
    }
    .status {
        font-size: 1rem;
    }
    .result-table th,
    .result-table td {
        padding: 0.5rem;
    }
}

.nice-result {
    max-width: 600px;
    margin: 40px auto;
    padding: 20px;
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
h2 {
    margin-bottom: 20px;
    text-align: center;
    color: #333;
}
.result-item {
    margin-bottom: 12px;
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid #eee;
    padding-bottom: 6px;
}
.label {
    font-weight: bold;
    color: #555;
    width: 150px;
}
.value {
    color: #222;
    word-break: break-all;
}
</style>
