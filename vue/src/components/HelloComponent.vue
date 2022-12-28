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
    <b-table-simple small caption-top stacked class="mt-3">
        <caption>
            Data Detail
        </caption>
        <b-thead head-variant="dark">
            <b-tr>
                <b-th colspan="2">Region</b-th>
                <b-th colspan="3">Clothes</b-th>
                <b-th colspan="2">Accessories</b-th>
            </b-tr>
            <b-tr>
                <b-th>Title</b-th>
                <b-th>Writer</b-th>
                <b-th>RegDate</b-th>
                <b-th>Memo</b-th>
            </b-tr>
        </b-thead>
        <b-tbody>
            <b-tr>
                <b-th rowspan="2" class="text-center">상세보기</b-th>
                <b-td stacked-heading="Title:" variant="default">{{ document.brdtitle }}</b-td>
                <b-td stacked-heading="Writer:" variant="default">{{ document.brdwriter }}</b-td>
                <b-td stacked-heading="RegDate:" variant="default">{{ document.brddate }}</b-td>
                <b-th rowspan="2" class="text-center">Memo</b-th>
                <b-td rowspan="2" class="text-center border-0">{{ document.brdmemo }}</b-td>
            </b-tr>
        </b-tbody>
        <b-tfoot>
            <b-tr>
                <b-td colspan="7" variant="secondary" class="text-right"> Total Rows: <b>5</b> </b-td>
            </b-tr>
        </b-tfoot>
    </b-table-simple>
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
        console.log('data : 데이터 초기화 준비');

        let document = JSON.parse(this.$route.params.document);

        return {
            data: 'HELLO', // [데이터 정의]
            document: document,
        };
    },
    // [생명 주기 : 라이프 사이클]
    beforeCreate() {
        console.log('beforeCreate : 인스턴스 초기화 준비');
    },
    created() {
        console.log('created : 인스턴스 생성 완료');
    },
    beforeMount() {
        console.log('beforeMount : DOM 렌더링 준비');
    },
    mounted() {
        console.log('mounted : DOM 렌더링 완료');
    },
    beforeUpdate() {
        console.log('beforeUpdate : DOM 상태 및 데이터 변경 시작');
    },
    updated() {
        console.log('updated : DOM 상태 및 데이터 변경 완료');
    },
    beforeUnmount() {
        console.log('beforeUnmount : 인스턴스 마운트 해제 준비');
    },
    unmounted() {
        console.log('unmounted : 인스턴스 마운트 해제 완료');
    },

    // [메소드 정의 실시]
    methods: {},
};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
.tb.detail {
    margin-top: 50px;
}
</style>
