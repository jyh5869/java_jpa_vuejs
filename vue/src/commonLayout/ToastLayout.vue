<!--
[FooterLayout.vue 설명]
1. 공통 : 푸터 레이아웃

2. template : 
   - 화면 상에 표시할 요소 작성 실시
   - 컴포넌트의 모든 마크업 구조와 디스플레이 로직 작성

3. script : 
   - import 구문을 사용해 template에서 사용할 컴포넌트 불러온다
   - export default 구문에서 모듈의 함수, 객체, 변수 등을 다른 모듈에서 가져다 사용 할 수 있도록 내보냅니다

4. style : 
   - 스타일 지정 실시
-->

<!-- [개별 템플릿 (뷰) 설정 실시] -->
<template>
    <div>
        <b-button @click="toast('b-toaster-top-right')" class="mb-2">b-toaster-top-right</b-button>
        <b-button @click="toast('b-toaster-top-left')" class="mb-2">b-toaster-top-left</b-button>
        <b-button @click="toast('b-toaster-top-center')" class="mb-2">b-toaster-top-center</b-button>
        <b-button @click="toast('b-toaster-top-full')" class="mb-2">b-toaster-top-full</b-button>
        <b-button @click="toast('b-toaster-bottom-right', true)" class="mb-2">b-toaster-bottom-right</b-button>
        <b-button @click="toast('b-toaster-bottom-left', true)" class="mb-2">b-toaster-bottom-left</b-button>
        <b-button @click="toast('b-toaster-bottom-center', true)" class="mb-2">b-toaster-bottom-center</b-button>
        <b-button @click="toast('b-toaster-bottom-full', true)" class="mb-2">b-toaster-bottom-full</b-button>

        <BToast />
    </div>
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
import { BToast } from 'bootstrap-vue-3';

export default {
    data() {
        return {
            counter: 0,
        };
    },
    components: {
        BToast,
    },
    props: ['parsingList'], // myArray prop을 정의하여 받는다
    methods: {
        toast(toaster, append = false) {
            console.log(toaster + ' /   ' + append);
            this.counter++;
        },
    },
};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
.title {
    background: #42b883;
    color: white;
    margin: 10px 0 20px 0;
    padding: 5px 0px;
    border-radius: 5px;
    font-size: 15px;
}

#nav .menu {
    color: white;
    padding: 0px;
    margin: 10px;
    text-decoration: none;
}
.addrAnalyResult-wrap {
    margin-top: 70px;
}

.addrAnalyResult-wrap ul {
    list-style: none;
    padding: 0 0em 0 0em;
    text-align: left;
}
.addrAnalyResult-wrap ul > li {
    padding: 2px 0px;
    text-align: center;
    font-size: 14px;
}
</style>
