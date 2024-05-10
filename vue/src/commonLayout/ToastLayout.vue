<template>
    <div class="toast-wrap p-2">
        <div class="toast-button">
            <b-button class="btn-sm" variant="outline-primary" @click="showToast">히스토리 보기</b-button>
        </div>
        <div class="toast-list">
            <ul>
                <li>
                    <b-toast v-model="toastVisible" :delay="toastData.delay" :auto-hide="toastData.autoHide" :no-fade="toastData.noFade" :no-close-button="toastData.noCloseButton" :variant="toastData.variant" :body-class="toastData.bodyClass" :header-class="toastData.headerClass" :title="toastData.title" :body="toastData.body"> </b-toast>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { BButton, BToast } from 'bootstrap-vue-3';

export default defineComponent({
    props: {
        showToastProp: {
            type: Boolean,
            default: false,
        },
        toastDataProp: {
            type: Object,
            default: () => ({
                delay: 3000, // 토스트가 자동으로 사라지는 지연 시간 (3초)
                autoHide: true, // 자동으로 숨기기
                noFade: false, // 페이드 없음
                noCloseButton: false, // 닫기 버튼 없음
                variant: 'success', // 토스트 배경 색상
                bodyClass: '', // 토스트 본문 클래스
                headerClass: '', // 토스트 헤더 클래스
                title: 'Toast Title', // 토스트 제목
                body: 'Toast Body', // 토스트 본문
            }),
        },
    },
    components: {
        BButton,
        BToast,
    },
    setup(props) {
        const toastVisible = ref(props.showToastProp);

        const showToast = () => {
            toastVisible.value = true;
        };

        return {
            showToast,
            toastVisible: props.toastDataProp,
            toastData: props.toastDataProp,
        };
    },
});
</script>
<style>
.toast-wrap {
    position: fixed;
    z-index: 10;
}
.toast-button {
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
