<template>
    <div class="toast-wrap p-2">
        <!-- 히스토리 홠성화 토글 버튼 Wrap -->
        <div class="toast-button" v-if="historyUseYn == true">
            <b-button class="btn-sm" variant="outline-primary" @click="toggleHistory">{{ toastHistoryTxt }}</b-button>
        </div>
        <!-- 토스트 창 Wrap -->
        <div class="toast-list">
            <ul>
                <li>
                    <b-list-group class="searchResult-wrap" v-if="toastVisible">
                        <b-list-group-item :variant="toastData.variant">
                            <b-toast v-model="toastVisible" :delay="toastData.delay" :auto-hide="toastData.autoHide" :no-fade="toastData.noFade" :no-close-button="toastData.noCloseButton" :variant="toastData.variant" :body-class="toastData.bodyClass" :header-class="toastData.headerClass" :title="toastData.title + ' [' + toastData.time + ']'">
                                {{ toastData.body }}
                            </b-toast>
                        </b-list-group-item>
                    </b-list-group>
                </li>
            </ul>
        </div>
        <!-- 히스토리 리스트 Wrap -->
        <div class="toast-list" id="History" v-if="toastHistory == true">
            <ul>
                <b-list-group class="searchResult-wrap">
                    <b-list-group-item :variant="item.variant" v-for="item in historyData" :key="item.title">
                        <li>
                            <b-toast v-model="toastHistory" :variant="item.variant" :auto-hide="false" :noFade="false" :title="item.title + ' [' + item.time + ']'" solid> {{ item.body }}</b-toast>
                        </li>
                    </b-list-group-item>
                </b-list-group>
            </ul>
        </div>
    </div>
</template>

<script>
import { ref, watch } from 'vue';
import { BToast } from 'bootstrap-vue-3';

export default {
    components: { BToast },
    props: {
        showToastProp: {
            type: Boolean,
            default: false,
        },
        toastDataProp: {
            type: Object,
            default: () => ({
                delay: 3000,
                autoHide: true,
                noFade: false,
                noCloseButton: false,
                variant: 'success',
                bodyClass: '',
                headerClass: '',
                title: 'Toast Title',
                body: 'Toast Body',
            }),
        },
        showHistoryProp: {
            type: Boolean,
            default: true,
        },

        historyUseYnProp: {
            type: Boolean,
            default: false,
        },
    },
    data(props) {
        const toastVisible = ref(props.showToastProp);
        const toastData = ref(props.toastDataProp);
        const toastHistory = ref(props.showHistoryProp);
        const historyData = ref(this.getCookie('history') || []);
        const toastHistoryTxt = ref('히스토리 열기');
        const historyUseYn = ref(props.historyUseYnProp);
        const userInfo = ref(this.$store.state);

        watch(
            () => props.showToastProp,
            (newVal) => {
                toastVisible.value = newVal;
            },
        );

        watch(
            () => props.toastDataProp,
            (newVal) => {
                let newToast = {
                    ...newVal, //토스트 정보
                    time: this.updateDate(), //스프레드 연산자를이용한 일자 SET
                    userInfo: { email: this.$store.state.email }, //이용자 아이디
                    //userInfo: this.$store.state, // 스프레드 연산자를이용한 유저정보 SET 이걸 usnerInfo로 바꾸면 왜안되는거야?
                    //userInfo: userInfo.value,
                };

                toastData.value = newToast;
                this.addToHistory(newToast);
            },
            { deep: true },
        );

        watch(
            () => props.showHistoryProp,
            (newVal) => {
                console.log('히스토리 이용상태 변경, 상태 : ' + newVal == true ? '사용' : '미사용');

                this.historyData.value = [];

                toastHistory.value = newVal; //이력 목록 활성화 여부
                historyUseYn.value = newVal; //이려 버튼 활성화 여부

                if (newVal == true) {
                    this.toastHistoryTxt = '히스토리 닫기';
                } else {
                    this.toastHistoryTxt = '히스토리 열기';
                }
            },
        );

        watch(
            () => props.historyUseYnProp,
            (newVal) => {
                console.log('히스토리 버튼 활성화 상태변경, 상태 : ' + newVal == true ? '사용' : '미사용');
                historyUseYn.value = newVal;
            },
        );

        return {
            toastVisible, //[토스트 뷰 플레그]
            toastData, //[토스트 뷰 데이터]
            toastHistory, //[토스트 이력 활성화 플레그]
            historyUseYn, //[토스트 이력 버튼 활성화 플레그]
            toastHistoryTxt, //[토스트 이력 버튼 텍스트]
            historyData, //[토스트 이력 데이터]
            userInfo, //[이용자 정보 데이터]
        };
    },
    methods: {
        //토스트 히스토리 파싱
        showToast() {
            this.toastHistory = !this.toastHistory;
            this.historyData = this.getCookie('history') || [];
        },
        //토스트 히스토리 버튼 클릭시 작동
        toggleHistory() {
            if (this.getCookie('history') == null) {
                alert('저장된 히스토리가 없습니다.');
            } else {
                this.toastHistory = !this.toastHistory;

                if (this.toastHistory == true) {
                    this.toastHistoryTxt = '히스토리 닫기';
                } else {
                    this.toastHistoryTxt = '히스토리 열기';
                }
            }
        },
        //토스트 히스토리 이력 추가
        addToHistory(toastData) {
            let history = this.getCookie('history') || [];

            history.push(toastData);

            console.log('Updated History:', history);

            this.setCookie('history', history, 3);
            this.historyData = history;
        },
        //쿠키 저장 (쿠키명, 쿠키값, 유지기간)
        setCookie(name, value, days) {
            const expires = new Date();
            expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
            document.cookie = `${name}=${encodeURIComponent(JSON.stringify(value))};expires=${expires.toUTCString()};path=/`;

            //console.log('Cookie Set:', `${name}=${encodeURIComponent(JSON.stringify(value))}`);
        },
        //쿠키 가져오기(쿠키명)
        getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) {
                try {
                    let data = JSON.parse(decodeURIComponent(parts.pop().split(';').shift()));

                    console.log(data.length);
                    /*
                    //1. Foreach를 사용해 토스트의 유저정보와 현재 유저정보를 비교하여 동일한 데이터만 파싱
                    data.forEach((element, index) => {
                        //console.log(index);
                        console.log(element);
                        let currentUser = this.$store.state.email;
                        let toastUser = element.userInfo.email;
                        console.log(index + ': 현재 접속 유저 : ' + currentUser + ' 토스트 작성 유저 : ' + toastUser);

                        //길이가 1일때는 스플라이스 불가능..?
                        if (currentUser != toastUser) {
                            console.log(index + ': 현재 접속 유저 : ' + currentUser + ' 토스트 작성 유저 : ' + toastUser);
                            data.splice(index, 1);
                        }
                    });
                    */

                    //2. filter 메소드 사용해 토스트의 유저정보와 현재 유저정보를 비교하여 동일한 데이터만 파싱
                    data = data.filter((element, index) => {
                        let currentUser = this.$store.state.email;
                        let toastUser = element.userInfo.email;

                        console.log(index + 1 + '번째 토스트 // 현재 접속 유저 : ' + currentUser + ' 토스트 작성 유저 : ' + toastUser);
                        return currentUser === toastUser && currentUser;
                    });

                    return data;
                } catch (e) {
                    console.error('Parsing error on', name);
                    return null;
                }
            }

            return null;
        },
        //제이슨 문자열 합치기
        mergeJsonStrings(jsonStr1, jsonStr2) {
            //JSON 문자열을 JavaScript 객체로 변환
            let obj1 = JSON.parse(jsonStr1);
            let obj2 = JSON.parse(jsonStr2);

            //두 객체를 병합
            let mergedObj = { ...obj1, ...obj2 };

            //병합된 객체를 JSON 문자열로 변환
            return JSON.stringify(mergedObj);
        },
        //현재 날짜 반환
        updateDate() {
            const date = new Date();
            const year = date.getFullYear();
            const month = ('0' + (date.getMonth() + 1)).slice(-2); // 월은 0부터 시작하므로 +1
            const day = ('0' + date.getDate()).slice(-2);
            const hours = ('0' + date.getHours()).slice(-2);
            const minutes = ('0' + date.getMinutes()).slice(-2);

            return (this.currentDate = `${year}-${month}-${day} ${hours}:${minutes}`);
        },
    },
};
</script>

<style>
.toast-wrap {
    position: fixed;
    z-index: 10;
}
.toast-list {
    margin: 0px;
}
.toast-list ul {
    padding: 0px;
}
.toast-list ul li {
    list-style: none;
    margin: 5px 0px;
}
</style>
