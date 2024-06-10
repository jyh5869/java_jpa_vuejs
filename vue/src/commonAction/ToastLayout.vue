<template>
    <div class="toast-wrap p-2">
        <div class="toast-button">
            <b-button class="btn-sm" variant="outline-primary" @click="toggleHistory">히스토리 보기</b-button>
        </div>
        <div class="toast-list">
            <ul>
                <li>
                    <b-toast v-model="toastVisible" :delay="toastData.delay" :auto-hide="toastData.autoHide" :no-fade="toastData.noFade" :no-close-button="toastData.noCloseButton" :variant="toastData.variant" :body-class="toastData.bodyClass" :header-class="toastData.headerClass" :title="toastData.title" @hidden="onToastHidden">
                        {{ toastData.body }}
                    </b-toast>
                </li>
            </ul>
        </div>
        <div class="toast-list" id="History" v-if="toastHistory == true">
            <ul>
                <b-list-group class="searchResult-wrap">
                    <b-list-group-item variant="secondary" v-for="item in historyData" :key="item.title">
                        <li>
                            <b-toast v-model="toastHistory" :variant="success" :auto-hide="false" :title="item.title" solid> {{ item.body }}</b-toast>
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

        watch(
            () => props.showToastProp,
            (newVal) => {
                toastVisible.value = newVal;
            },
        );

        watch(
            () => props.toastDataProp,
            (newVal) => {
                toastData.value = newVal;

                //let history = getCookie();
                //this.setCookie('history', newVal, 3);
                this.addToHistory(newVal);
                //let history = this.getCookie('history');
                //this.historyData = [this.getCookie('history')];
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
            historyData,
        };
    },
    methods: {
        showToast() {
            console.log(this.toastHistory);
            this.toastHistory = !this.toastHistory;

            this.historyData = [this.getCookie('history')];
        },
        toggleHistory() {
            this.toastHistory = !this.toastHistory;
        },
        addToHistory(toastData) {
            let history = this.getCookie('history') || [];
            history.push(toastData);
            this.setCookie('history', history, 3);
            this.historyData = history;
        },
        setCookie(name, value, days) {
            const expires = new Date();
            expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
            document.cookie = `${name}=${encodeURIComponent(JSON.stringify(value))};expires=${expires.toUTCString()};path=/`;
        },
        getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) {
                try {
                    return JSON.parse(decodeURIComponent(parts.pop().split(';').shift()));
                } catch (e) {
                    console.error('Parsing error on', name);
                    return null;
                }
            }
            return null;
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
