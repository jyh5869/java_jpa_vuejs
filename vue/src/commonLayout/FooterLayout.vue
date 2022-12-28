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
    <br /><br />
    <div class="footer">Vue Application</div>
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
.footer {
    background: #42b883;
    color: white;
    margin: auto;
    padding: 15px;
}

#nav .menu {
    color: white;
    padding: 0px;
    margin: 10px;
    text-decoration: none;
}
</style>
