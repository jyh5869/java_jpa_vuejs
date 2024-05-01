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
    <div class="addrAnalyResult-wrap">
        <div class="title">{{ parsingList[0] }}</div>
        <div>
            <ul>
                <li v-for="(item, index) in parsingList[1]" :key="index">{{ item }}</li>
            </ul>
        </div>
    </div>
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {
    props: ['parsingList'], // myArray prop을 정의하여 받는다
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
