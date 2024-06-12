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
                            <b-toast v-model="toastVisible" :delay="toastData.delay" :auto-hide="toastData.autoHide" :no-fade="toastData.noFade" :no-close-button="toastData.noCloseButton" :variant="toastData.variant" :body-class="toastData.bodyClass" :header-class="toastData.headerClass" :title="toastData.title" @hidden="onToastHidden">
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
                            <b-toast v-model="toastHistory" :variant="item.variant" :auto-hide="false" :noFade="false" :title="item.title" solid> {{ item.body }}</b-toast>
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

                this.addToHistory(newVal);
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
        };
    },
    methods: {
        showToast() {
            console.log(this.toastHistory);
            this.toastHistory = !this.toastHistory;

            this.historyData = [this.getCookie('history')];
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
