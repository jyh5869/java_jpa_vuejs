<template>
    <div>
        <h1 style="margin: 50px 0px 50px 0px">통합본인인증 Sample</h1>

        <form ref="saForm" target="sa_popup" method="post" action="https://sa.inicis.com/auth">
            <table>
                <tr>
                    <td><h4>mid</h4></td>
                    <td><input type="text" name="mid" :value="form.mid" readonly /></td>
                </tr>
                <tr>
                    <td><h4>reqSvcCd</h4></td>
                    <td><input type="text" name="reqSvcCd" :value="form.reqSvcCd" readonly /></td>
                </tr>
                <tr>
                    <td><h4>identifier</h4></td>
                    <td><input type="text" name="identifier" value="테스트서명입니다." readonly /></td>
                </tr>
                <tr>
                    <td><h4>mTxId</h4></td>
                    <td><input type="text" name="mTxId" :value="form.mTxId" readonly /></td>
                </tr>
                <tr>
                    <td><h4>authHash</h4></td>
                    <td><input type="text" name="authHash" :value="form.authHash" readonly /></td>
                </tr>
                <tr>
                    <td><h4>flgFixedUser</h4></td>
                    <td><input type="text" name="flgFixedUser" :value="form.flgFixedUser" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userName</h4></td>
                    <td><input type="text" name="userName" :value="form.userName" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userPhone</h4></td>
                    <td><input type="text" name="userPhone" :value="form.userPhone" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userBirth</h4></td>
                    <td><input type="text" name="userBirth" :value="form.userBirth" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userHash</h4></td>
                    <td><input type="text" name="userHash" :value="form.userHash" readonly /></td>
                </tr>
                <tr>
                    <td><h4>reservedMsg</h4></td>
                    <td><input type="text" name="reservedMsg" :value="form.reservedMsg" readonly /></td>
                </tr>
                <tr>
                    <td><h4>directAgency</h4></td>
                    <td><input type="text" name="directAgency" value="" /></td>
                </tr>
                <tr>
                    <td><h4>successUrl</h4></td>
                    <td><input type="text" name="successUrl" :value="form.successUrl" readonly /></td>
                </tr>
                <tr>
                    <td><h4>failUrl</h4></td>
                    <td><input type="text" name="failUrl" :value="form.failUrl" readonly /></td>
                </tr>
            </table>
        </form>

        <div style="margin-top: 30px; text-align: left">
            <button class="Btn" @click="callSa">인증요청</button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const form = ref({});
const saForm = ref(null);

onMounted(async () => {
    try {
        const userInfo = {
            userName: '조영현',
            userPhone: '01049255869',
            userBirth: '19910604',
        };
        const { data } = await axios.post('/api/noAuth/generate-request-info', userInfo);
        console.log(data);
        form.value = {
            mid: data.mid,
            reqSvcCd: data.reqSvcCd,
            mTxId: data.mTxId,
            authHash: data.authHash,
            flgFixedUser: data.flgFixedUser,
            userName: data.userName,
            userPhone: data.userPhone,
            userBirth: data.userBirth,
            userHash: data.userHash,
            reservedMsg: data.reservedMsg,
            successUrl: data.successUrl,
            failUrl: data.failUrl,
        };
    } catch (err) {
        console.error('KG이니시스 인증 데이터 로딩 실패:', err);
    }
});

const callSa = () => {
    const width = 400;
    const height = 640;
    const xPos = window.screen.width / 2 - width / 2;
    const yPos = window.screen.height / 2 - height / 2;
    const popup = window.open('', 'sa_popup', `width=${width}, height=${height}, left=${xPos}, top=${yPos}, resizable=yes, scrollbars=yes`);

    if (popup) {
        setTimeout(() => {
            if (saForm.value) {
                saForm.value.submit();
            } else {
                console.error('saForm is undefined');
            }
        }, 300);
    } else {
        alert('팝업이 차단되었습니다. 브라우저 설정을 확인해주세요.');
    }
};
</script>

<style scoped>
h4 {
    width: 150px;
}
input {
    padding: 7px;
    width: 500px;
    margin-bottom: 10px;
}
.Btn {
    display: inline-block;
    font-size: 17px;
    color: #fff;
    font-weight: bold;
    background: #333;
    height: 48px;
    line-height: 44px;
    min-width: 380px;
}
</style>
