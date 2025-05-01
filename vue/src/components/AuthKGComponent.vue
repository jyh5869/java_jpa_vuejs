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
                    <td>
                        <input type="text" name="identifier" value="테스트서명입니다." readonly />
                    </td>
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
                    <td>
                        <input type="text" name="flgFixedUser" :value="form.flgFixedUser" readonly />
                    </td>
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
                    <td>
                        <input type="text" name="reservedMsg" :value="form.reservedMsg" readonly />
                    </td>
                </tr>
                <tr>
                    <td><h4>directAgency</h4></td>
                    <td><input type="text" name="directAgency" value="" /></td>
                </tr>
                <tr>
                    <td><h4>successUrl</h4></td>
                    <td>
                        <input type="text" name="successUrl" :value="form.successUrl" readonly />
                    </td>
                </tr>
                <tr>
                    <td><h4>failUrl</h4></td>
                    <td>
                        <input type="text" name="failUrl" :value="form.failUrl" readonly />
                    </td>
                </tr>
            </table>
        </form>

        <div style="margin-top: 30px; text-align: left">
            <button class="Btn" @click="callSa">인증요청</button>
        </div>
    </div>

    <div>
        <button @click="requestNiceAuth">본인인증</button>
        <form ref="niceForm" method="post" target="popupChk" action="https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb">
            <input type="hidden" name="EncodeData" :value="encodeData" />
            <input type="hidden" name="m" value="checkplusService" />
        </form>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const form = ref({});
const saForm = ref(null);
// 팝업 윈도우 레퍼런스를 저장할 변수
let popupWindow = null;

/**
 * 팝업으로부터 postMessage를 받아 처리하는 핸들러
 */
function handleMessage(event) {
    // 팝업인지 확인만 하고 origin 검사는 생략
    if (event.source !== popupWindow) return;

    const msg = event.data;
    console.log(' ☆ ☆ ☆ ☆ ☆ ☆ ☆ ');
    console.log(msg);
    if (msg?.type !== 'INICIS_AUTH_COMPLETE' && msg?.type !== 'NICE_AUTH_COMPLETE') return;

    if (msg?.type == 'INICIS_AUTH_COMPLETE') {
        const payload = msg.payload || {};
        console.log('KG이니시스 인증 결과:', payload);
        if (payload.redirectTo) {
            // 3) 받은 모든 파라미터를 query 로 붙여서 라우팅
            router.push({
                path: payload.redirectTo,
                query: payload,
            });
        }
    }

    if (msg?.type == 'NICE_AUTH_COMPLETE') {
        const payload = msg.payload || {};
        console.log('NICE 인증 결과:', payload);
        if (payload.redirectTo) {
            // 3) 받은 모든 파라미터를 query 로 붙여서 라우팅
            router.push({
                path: payload.redirectTo,
                query: payload,
            });
        }
    }

    window.removeEventListener('message', handleMessage);
}

onMounted(async () => {
    // 컴포넌트 마운트 시 메시지 리스너 등록
    window.addEventListener('message', handleMessage);

    // 인증 요청에 필요한 파라미터를 서버에서 받아 설정
    try {
        const userInfo = {
            userName: '조영현',
            userPhone: '01049255869',
            userBirth: '19910604',
            redirectTo: '/AuthKGResComponent',
        };
        const { data } = await axios.post('/api/noAuth/generate-request-info', userInfo);
        // form에 서버 응답값 세팅
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

onUnmounted(() => {
    // 컴포넌트 언마운트 시 리스너 제거
    window.removeEventListener('message', handleMessage);
});

/**
 * 팝업 창을 열고, 300ms 후에 form을 submit
 */
const callSa = () => {
    const width = 400;
    const height = 640;
    const left = window.screen.width / 2 - width / 2;
    const top = window.screen.height / 2 - height / 2;

    // 팝업 열기 및 레퍼런스 저장
    popupWindow = window.open('', 'sa_popup', `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);

    if (!popupWindow) {
        alert('팝업이 차단되었습니다. 브라우저 설정을 확인해주세요.');
        return;
    }

    // 팝업이 준비될 시간을 잠시 확보한 뒤 전송
    setTimeout(() => {
        if (saForm.value) {
            saForm.value.submit();
        } else {
            console.error('saForm이 undefined 입니다.');
        }
    }, 300);
};

/*
 *
 *
 * 나이스 인증
 *
 */
const encodeData = ref('');
const niceForm = ref(null);

const requestNiceAuth = async () => {
    try {
        const userInfo = {
            userName: '조영현',
            userPhone: '01049255869',
            userBirth: '19910604',
            redirectTo: '/AuthKGResComponent',
        };

        const res = await axios.post('/api/noAuth/request/nice', userInfo);
        encodeData.value = res.data.encData;
        console.log(res.data.encData);
        const popup = window.open('', 'popupChk', 'width=500,height=550,top=100,left=100');
        if (popup && niceForm.value) {
            setTimeout(() => {
                niceForm.value.submit();
            }, 200);
        } else {
            alert('팝업이 차단되었습니다.');
        }
    } catch (e) {
        alert('본인인증 요청 중 오류 발생');
        console.error(e);
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
