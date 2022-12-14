<!--
[App.vue 설명]
1. App.vue 는 Vue 애플리케이션의 최상위 컴포넌트입니다

2. template : 
   - 화면 상에 표시할 요소 작성 실시
   - 컴포넌트의 모든 마크업 구조와 디스플레이 로직 작성

3. script : 
   - import 구문을 사용해 template에서 사용할 컴포넌트 불러온다
   - export default 구문에서 해당 App 컴포넌트가 main.js 파일에서 불러와지도록 내용 작성 실시
   - export default 구문에서 모듈의 함수, 객체, 변수 등을 다른 모듈에서 가져다 사용 할 수 있도록 내보냅니다

4. style : 
   - 스타일 지정 실시

5. props : 부모가 전달한 데이터를 받을 때 사용합니다 (부모 쪽에서 자식 객체 생성 필요) : 자식쪽에서 동적 변경 불가능

6. {{ message }} : 데이터 바인딩 시 사용

7. @click : 클릭 이벤트 적용 약어 (원본 : v-on:click)

8. data : 컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)

9. methods : 메소드 정의 실시

10. this.$parent.부모 메소드 명칭 : 부모 메소드 호출 실시

11. v-model : 데이터 양방향 바인드를 실시합니다 (ex: input text 수정 시 <h1> 태그 값도 동시 변경)

12. v-html : 원시 HTML 형식 문자열 데이터를 실제 HTML 로 변경해줍니다

13. v-bind : disabled : 버튼 사용 가능 여부 (클릭) 처리 설정

14. v-for : 배열 데이터를 for 반복문을 돌면서 순차적으로 확인합니다

15. v-bind:class : 특정 조건을 만족 (true) 할 시 삼항식을 사용해 클래스 지정 분기 처리 실시

16. v-bind:style : css 스타일 코드를 지정할 수 있습니다

17. v-if : 조건 값에 따라서 렌더링 (표시) 를 수행합니다

18. v-if v-else : 조건 값이 true 일 경우 if 분기 처리 false 일 경우 else 분기 처리를 수행합니다

19. v-show : v-if 문과 유사 하지만, 초기 렌더링 시 true, false 상관 없이 무조건 dom에 렌더링 됩니다

20. @click.prevent : 원래 동작하는 클릭 이벤트 막고 , 지정한 메소드로 이벤트 핸들링 수행

21. @keydown : 키 입력 이벤트 값을 확인 할 수 있습니다 (한글 경우는 Process 가 출력됨)

22. @input : 특정 변수에 입력한 값을 대입 할 수 있습니다

23. watch : data 및 computed 속성의 값이 변경 된 상태 감지 실시

24. style scoped : scoped 를 써주어야 해당 컴포넌트의 요소들에만 스타일이 적용됩니다 . (아니면 프로젝트 전체 요소에 스타일 적용됨)

25. router-view : 라우터에서 설정한 뷰 로드 실시
-->

<!-- [애플리케이션 공통 템플릿 (뷰) 지정] -->
<template>
    <!-- [App.vue 데이터 바인딩 지정] -->
    <div id="dataContainer">
        <h1><img src="./assets/logo.png" id="icon" alt="User Icon" />{{ data }}</h1>
    </div>

    <!-- [고정 : 헤더 컴포넌트] -->
    <HeaderLayout />
    <b-row>
        <b-col md="8" offset-md="2">
            <!-- [동적 : 라우터 뷰 컴포넌트] -->
            <router-view />
        </b-col>
    </b-row>
    <!-- [고정 : 푸터 컴포넌트] -->
    <FooterLayout />
</template>

<!-- [애플리케이션 공통 스크립트 지정] -->
<script>
// [Component 지정]
import HeaderLayout from './commonLayout/HeaderLayout.vue';
import FooterLayout from './commonLayout/FooterLayout.vue';
import HomeComponent from './components/HomeComponent.vue';

// [export 설정 실시]
export default {
    // [main.js 등록한 App 컴포넌트]
    name: 'App',

    // [동적 변환 컴포넌트 페이지 : 기본 표시 부분]
    components: {
        HeaderLayout, // [HeaderLayout 컴포넌트]
        FooterLayout, // [FooterLayout 컴포넌트]
    },

    // [컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)]
    data() {
        return {
            data: 'APP VUE', // [데이터 지정]

            HomeComponent, // [초기 로드 컴포넌트 지정]
        };
    },
    /*
    // [생명 주기 : 라이프 사이클]
    beforeCreate() {
        console.log('');
        console.log('[App] : [beforeCreate] : [start]');
        console.log('설 명 : 인스턴스 초기화 준비');
        console.log('');
    },
    created() {
        console.log('');
        console.log('[App] : [created] : [start]');
        console.log('설 명 : 인스턴스 생성 완료');
        console.log('');
    },
    beforeMount() {
        console.log('');
        console.log('[App] : [beforeMount] : [start]');
        console.log('설 명 : DOM 렌더링 준비');
        console.log('');
    },
    mounted() {
        console.log('');
        console.log('[App] : [mounted] : [start]');
        console.log('설 명 : DOM 렌더링 완료');
        console.log('');
    },
    beforeUpdate() {
        console.log('');
        console.log('[App] : [beforeUpdate] : [start]');
        console.log('설 명 : DOM 상태 및 데이터 변경 시작');
        console.log('');
    },
    updated() {
        console.log('');
        console.log('[App] : [updated] : [start]');
        console.log('설 명 : DOM 상태 및 데이터 변경 완료');
        console.log('');
    },
    beforeUnmount() {
        console.log('');
        console.log('[App] : [beforeUnmount] : [start]');
        console.log('설 명 : 인스턴스 마운트 해제 준비');
        console.log('');
    },
    unmounted() {
        console.log('');
        console.log('[App] : [unmounted] : [start]');
        console.log('설 명 : 인스턴스 마운트 해제 완료');
        console.log('');
    },
    */
    // [메소드 정의 실시]
    methods: {},
};
</script>

<!-- [애플리케이션 공통 스타일 지정] -->
<style>
#dataContainer {
    color: #42b883;
}

#dataContainer h1 {
    display: inline-block;
}

#dataContainer img {
    width: 9.5%;
}

#main {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
}
</style>
