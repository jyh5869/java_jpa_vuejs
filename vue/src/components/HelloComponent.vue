<!--
[HelloComponent.vue 설명]
1. App.vue 에 포함된 자식 컴포넌트입니다

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
    <hr />
    <!-- [data : 데이터 바인딩 지정] -->
    <div>
        <h1>{{ data }}</h1>
    </div>
    <hr />
    <!-- [이미지 설정 실시] -->
    <div>
        <img src="../assets/logo.png" />
    </div>
    <div>
        <h2>상세보기</h2>
        <h2>제목 : {{ document.brdtitle }}</h2>
        <h2>작성자: {{ document.brdwriter }}</h2>
        <h2>날짜 : {{ document.brddate }}</h2>
        <h2>내용 : {{ document.brdmemo }}</h2>
    </div>
    <hr />
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {
    name: 'HelloComponent',
    // [부모에서 전달 받은 데이터 : 자식에서 동적 수정 불가능]
    // [형태 : <HelloComponent msg="HelloComponent"/>]
    props: {
        props: {
            name: {
                type: String,
                default: '',
            },
            age: {
                type: Number,
                default: 0,
            },
        },
    },

    // [컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)]
    data() {
        let document = JSON.parse(this.$route.params.document);

        console.log('');
        console.log('[HelloComponent] : [data] : [start]');
        console.log('설 명 : 데이터 초기화 준비');
        console.log('');

        return {
            data: 'HELLO', // [데이터 정의]
            document: document,
        };
    },

    // [생명 주기 : 라이프 사이클]
    beforeCreate() {
        console.log('');
        console.log('[HelloComponent] : [beforeCreate] : [start]');
        console.log('설 명 : 인스턴스 초기화 준비');
        console.log('');
    },
    created() {
        console.log('');
        console.log('[HelloComponent] : [created] : [start]');
        console.log('설 명 : 인스턴스 생성 완료');
        console.log('');
    },
    beforeMount() {
        console.log('');
        console.log('[HelloComponent] : [beforeMount] : [start]');
        console.log('설 명 : DOM 렌더링 준비');
        console.log('');
    },
    mounted() {
        console.log('');
        console.log('[HelloComponent] : [mounted] : [start]');
        console.log('설 명 : DOM 렌더링 완료');
        console.log('');
    },
    beforeUpdate() {
        console.log('');
        console.log('[HelloComponent] : [beforeUpdate] : [start]');
        console.log('설 명 : DOM 상태 및 데이터 변경 시작');
        console.log('');
    },
    updated() {
        console.log('');
        console.log('[HelloComponent] : [updated] : [start]');
        console.log('설 명 : DOM 상태 및 데이터 변경 완료');
        console.log('');
    },
    beforeUnmount() {
        console.log('');
        console.log('[HelloComponent] : [beforeUnmount] : [start]');
        console.log('설 명 : 인스턴스 마운트 해제 준비');
        console.log('');
    },
    unmounted() {
        console.log('');
        console.log('[HelloComponent] : [unmounted] : [start]');
        console.log('설 명 : 인스턴스 마운트 해제 완료');
        console.log('');
    },

    // [메소드 정의 실시]
    methods: {},
};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
.colorRed {
    color: red;
}

.colorBlue {
    color: blue;
}
</style>
