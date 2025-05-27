<template>
    <h1>통합본인인증 관리 페이지</h1>
    <div class="contents">
        <h3>KG 간편인증</h3>
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

        <div class="btn-wrap">
            <button class="Btn" @click="requestKGAuth">KG이니시스 간편인증</button>
        </div>

        <div class="btn-wrap">
            <button class="Btn" @click="requestKGAuth2">KG이니시스 간편인증[백엔드에서 서브밋]</button>
        </div>
    </div>

    <div class="contents">
        <h3>NICE 본인인증</h3>
        <form ref="niceForm" target="none" method="post">
            <table>
                <tr>
                    <td><h4>siteCode</h4></td>
                    <td><input type="text" name="siteCode" :value="formNice.siteCode" readonly /></td>
                </tr>
                <tr>
                    <td><h4>sitePassword</h4></td>
                    <td><input type="text" name="sitePassword" :value="formNice.sitePassword" readonly /></td>
                </tr>
                <tr>
                    <td><h4>identifier</h4></td>
                    <td>
                        <input type="text" name="identifier" value="테스트서명입니다." readonly />
                    </td>
                </tr>
                <tr>
                    <td><h4>sRequestNumber</h4></td>
                    <td><input type="text" name="sRequestNumber" :value="formNice.sRequestNumber" readonly /></td>
                </tr>
                <tr>
                    <td><h4>authHash</h4></td>
                    <td><input type="text" name="authHash" value="해당없음" readonly /></td>
                </tr>
                <tr>
                    <td><h4>flgFixedUser</h4></td>
                    <td>
                        <input type="text" name="flgFixedUser" value="해당없음" readonly />
                    </td>
                </tr>
                <tr>
                    <td><h4>userName</h4></td>
                    <td><input type="text" name="userName" :value="formNice.userName" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userPhone</h4></td>
                    <td><input type="text" name="userPhone" :value="formNice.userPhone" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userBirth</h4></td>
                    <td><input type="text" name="userBirth" :value="formNice.userBirth" readonly /></td>
                </tr>
                <tr>
                    <td><h4>userHash</h4></td>
                    <td><input type="text" name="userHash" value="해당없음" readonly /></td>
                </tr>
                <tr>
                    <td><h4>redirectTo</h4></td>
                    <td>
                        <input type="text" name="redirectTo" :value="formNice.redirectTo" readonly />
                    </td>
                </tr>
                <tr>
                    <td><h4>returnUrl</h4></td>
                    <td>
                        <input type="text" name="returnUrl" :value="formNice.returnUrl" readonly />
                    </td>
                </tr>
                <tr>
                    <td><h4>errorUrl</h4></td>
                    <td>
                        <input type="text" name="errorUrl" :value="formNice.errorUrl" readonly />
                    </td>
                </tr>
            </table>
        </form>

        <form ref="niceForm" method="post" target="popupChk" action="https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb">
            <input type="hidden" name="EncodeData" :value="encodeData" />
            <input type="hidden" name="m" value="checkplusService" />
        </form>

        <div class="btn-wrap">
            <button class="Btn" @click="requestNiceAuth">나이스 본인인증</button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const form = ref({}); //KG 인증 요청정보
const formNice = ref({}); //나이스 인증 요청정보
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
    } else if (msg?.type == 'NICE_AUTH_COMPLETE') {
        alert('나이스');
        const payload = msg.payload || {};
        console.log('NICE 인증 결과:', payload);
        if (payload.redirectTo) {
            // 3) 받은 모든 파라미터를 query 로 붙여서 라우팅
            router.push({
                path: payload.redirectTo,
                query: payload,
            });
        }
    } else {
        if (msg.authProvider == 'KG') {
            alert('KG이니시스 간편인증이 실패했습니다.\n잠시 후 다시 시도해주세요.');
        } else if (msg.authProvider == 'NICE') {
            alert('NICE 본인인증이 실패했습니다.\n잠시 후 다시 시도해주세요.');
        } else {
            alert('본인인증이 실패했습니다.\n잠시 후 다시 시도해주세요.');
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

    try {
        const userInfo = {
            userName: '조영현',
            userPhone: '01049255869',
            userBirth: '19910604',
            redirectTo: '/AuthKGResComponent',
        };

        const res = await axios.post('/api/noAuth/request/nice', userInfo);
        encodeData.value = res.data.encData;

        formNice.value = {
            siteCode: res.data.siteCode,
            sitePassword: res.data.sitePassword,
            returnUrl: res.data.returnUrl,
            errorUrl: res.data.errorUrl,
            authType: res.data.authType,
            customize: res.data.customize,
            sRequestNumber: res.data.sRequestNumber,
            userName: userInfo.userName,
            userPhone: userInfo.userPhone,
            userBirth: userInfo.userBirth,
            redirectTo: res.data.redirectTo,
        };
    } catch (err) {
        console.error('NICE 인증 데이터 로딩 실패:', err);
    }
});

onUnmounted(() => {
    // 컴포넌트 언마운트 시 리스너 제거
    window.removeEventListener('message', handleMessage);
});

/**
 * 팝업 창을 열고, 300ms 후에 form을 submit
 */
const requestKGAuth = () => {
    const width = 400;
    const height = 640;
    const left = window.screen.width / 2 - width / 2;
    const top = window.screen.height / 2 - height / 2;

    // 팝업 열기 및 레퍼런스 저장
    popupWindow = window.open('', 'kg_popup', `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);

    if (!popupWindow) {
        alert('팝업이 차단되었습니다. 브라우저 설정을 확인해주세요.');
        return;
    }

    // 팝업이 준비될 시간을 잠시 확보한 뒤 전송
    setTimeout(() => {
        if (saForm.value) {
            saForm.value.submit();
        } else {
            console.error('인증 데이터가 정의되지 않았거나 값이 부정확합니다.\n잠시 후 다시 시도해주시기 바랍니다.');
        }
    }, 300);
};

const requestKGAuth2 = async () => {
    const width = 400;
    const height = 640;
    const left = window.screen.width / 2 - width / 2;
    const top = window.screen.height / 2 - height / 2;

    // 팝업 열기 및 레퍼런스 저장

    const userInfo = {
        userName: '조영현',
        userPhone: '01049255869',
        userBirth: '19910604',
        redirectTo: '/AuthKGResComponent',
    };

    const res = await axios.post('/api/noAuth/kgAuthStart', userInfo, {
        responseType: 'text',
    });

    if (res.data != '') {
        console.log('팝업열게');

        console.log(res.data);
        popupWindow = window.open('', 'sa_popup', `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);

        if (!popupWindow) {
            alert('팝업이 차단되었습니다. 브라우저 설정을 확인해주세요.');
            return;
        }

        popupWindow.document.write(res.data);
        popupWindow.document.close(); // <- 이거 빠지면 onload 이벤트 작동 안 함
    } else {
        console.log('팝업 데이터가 없어여');
    }
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
        popupWindow = window.open('', 'popupChk', 'width=500,height=550,top=100,left=100, resizable=yes,scrollbars=yes`');

        if (popupWindow && niceForm.value) {
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
h1 {
    margin: 50px 0px 50px 0px;
}
h3 {
    margin: 30px 0px 30px 0px;
}
h4 {
    width: 100%;
    font-size: 20px;
}
table {
    width: 100%;
}
input {
    padding: 7px;
    margin-bottom: 10px;
    width: 100%;
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
    width: 80%;
    text-align: center;
}
.contents {
    display: inline-block;
    width: 50%;
    vertical-align: top;
}
.contents .btn-wrap {
    margin: 10px 0px 50px 0px;
}

@media (max-width: 1200px) {
    .contents {
        display: block;
        width: 100%;
    }
    .contents .btn-wrap .Btn {
        width: 100%;
    }
}
</style>
