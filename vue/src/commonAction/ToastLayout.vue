<template>
    <div class="toast-wrap p-2">
        <div class="toast-button">
            <b-button class="btn-sm" variant="outline-primary" @click="toggleHistory">{{ toastHistoryTxt }}</b-button>
        </div>
        <div class="toast-list">
            <ul>
                <li>
                    <b-list-group class="searchResult-wrap" v-if="toastVisible">
                        <b-list-group-item :variant="toastData.variant">
                            <b-toast v-model="toastVisible" :delay="toastData.delay" :auto-hide="toastData.autoHide" :no-fade="toastData.noFade" :no-close-button="toastData.noCloseButton" :variant="toastData.variant" :body-class="toastData.bodyClass" :header-class="toastData.headerClass" :title="toastData.title + ' [' + toastData.time + ']'" @hidden="onToastHidden">
                                {{ toastData.body }}
                            </b-toast>
                        </b-list-group-item>
                    </b-list-group>
                </li>
            </ul>
        </div>
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
    },
    data(props) {
        const toastVisible = ref(props.showToastProp);
        const toastData = ref(props.toastDataProp);
        const toastHistory = ref(true);
        const historyData = ref(this.getCookie('history') || []);
        const toastHistoryTxt = ref('히스토리 열기');
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
                    time: this.updateDate(), // 스프레드 연산자를이용한 일자 SET
                    userInfo: { email: this.$store.state.email },
                    //userInfo: this.$store.state, // 스프레드 연산자를이용한 유저정보 SET 이걸 usnerInfo로 바꾸면 왜안되는거야?
                    //userInfo: userInfo.value,
                };

                toastData.value = newToast;
                this.addToHistory(newToast);
            },
            { deep: true },
        );

        const onToastHidden = () => {
            console.log('Toast hidden, executing function...');
            // 여기에 원하는 함수를 실행합니다.
        };

        return {
            toastVisible,
            toastData,
            onToastHidden,
            toastHistory,
            toastHistoryTxt,
            historyData,
            userInfo,
        };
    },
    methods: {
        showToast() {
            this.toastHistory = !this.toastHistory;
            this.historyData = this.getCookie('history') || [];
        },
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
        addToHistory(toastData) {
            let history = this.getCookie('history') || [];

            history.push(toastData);

            console.log('Updated History:', history);
            this.setCookie('history', history, 3);
            this.historyData = history;
        },
        setCookie(name, value, days) {
            const expires = new Date();
            expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
            document.cookie = `${name}=${encodeURIComponent(JSON.stringify(value))};expires=${expires.toUTCString()};path=/`;

            console.log('Cookie Set:', `${name}=${encodeURIComponent(JSON.stringify(value))}`);
        },
        getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) {
                try {
                    let data = JSON.parse(decodeURIComponent(parts.pop().split(';').shift()));

                    console.log(data.length);
                    /*
                    //토스트의 유저정보와 현재 유저정보를 비교하여 동일한 데이터만 파싱
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

                    // 또는 filter 메소드 사용
                    data = data.filter((element, index) => {
                        let currentUser = this.$store.state.email;
                        let toastUser = element.userInfo.email;

                        console.log(index + 1 + '번째 토스트 // 현재 접속 유저 : ' + currentUser + ' 토스트 작성 유저 : ' + toastUser);
                        return currentUser === toastUser;
                    });

                    return data;
                } catch (e) {
                    console.error('Parsing error on', name);
                    return null;
                }
            }
            console.log('이고먀냐냐냐냐ㅑ');
            return null;
        },
        mergeJsonStrings(jsonStr1, jsonStr2) {
            // JSON 문자열을 JavaScript 객체로 변환
            let obj1 = JSON.parse(jsonStr1);
            let obj2 = JSON.parse(jsonStr2);

            // 두 객체를 병합
            let mergedObj = { ...obj1, ...obj2 };

            // 병합된 객체를 JSON 문자열로 변환
            return JSON.stringify(mergedObj);
        },
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
