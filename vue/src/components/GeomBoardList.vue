<!--
[MainComponent.vue 설명]
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
<!--
    ※ 참고자료 : 리스트 파싱 URL - https://onethejay.tistory.com/68
-->
<!-- [개별 템플릿 (뷰) 설정 실시] -->
<template>
    <!-- [data : 데이터 바인딩 지정] -->

    <div>
        <b-table :items="dataList" :fields="dataFields" :busy="isBusy" class="mt-3" outlined hover responsive="sm" caption-top>
            <template #table-busy>
                <div class="text-center text-danger my-2 mt-5">
                    <b-spinner class="align-middle mx-3 mx-1"></b-spinner>
                    <strong>Loading...</strong>
                </div>
            </template>
            <template #cell(board_Sq)="board_Sq"> {{ board_Sq.index + 1 }} </template>
            <template #cell(title)="title">
                <!-- <a href="#" v-on:click="viewDetail(title.index, $event)" class="text-primary text-decoration-none">{{ title.value == null ? 'No Title' : title.value }}</a> -->
                <router-link
                    class="text-secondary text-decoration-none"
                    :to="{
                        name: 'GeomBoardEdit',
                        params: {
                            document: JSON.stringify({
                                boardData: this.dataList[title.index],
                                callType: 'Detail',
                            }),
                        },
                    }"
                    >{{ title.value == null ? 'No Title' : title.value }}
                </router-link>
            </template>
            <template #cell(reg_dt)="reg_dt"> {{ reg_dt.value }} </template>
            <template #table-caption>Data List</template>
        </b-table>
    </div>

    <div v-html="pagination"></div>

    <div class="col-12">
        <button type="button" class="btn float-right btn-success" @click="getBoardWrite">글쓰기</button>
    </div>
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {
    name: 'MainComponent',
    // [부모에서 전달 받은 데이터 : 자식에서 동적 수정 불가능]
    // [형태 : <MainComponent msg="MainComponent"/>]
    props: {
        msg: String,
    },

    // [컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)]
    data() {
        return {
            data: 'MAIN', // [데이터 정의]
            isBusy: true,
            dataFields: [
                {
                    key: 'board_Sq',
                    sortable: true,
                    label: '글번호',
                    variant: '',
                },
                {
                    key: 'title',
                    label: '제목',
                    sortable: true,
                    variant: '',
                },
                {
                    key: 'userNm',
                    label: '작성자',
                    sortable: false,
                    variant: '',
                },
                {
                    key: 'createdDate',
                    label: '작성일',
                    sortable: true,
                    variant: '',
                },
            ],
            dataList: [],
            pagination: null,
            totalCount: 0, //총 개시물 갯수
            currentPage: 0, //현재 페이지 정보
        };
    },
    mounted() {
        this.getList();
    },
    created() {
        window.getList = this.getList;
    },
    // [메소드 정의 실시]
    methods: {
        // 글쓰기 페이지로 이동
        getBoardWrite: async function () {
            this.$router.push({
                name: 'GeomBoardEdit',
                params: {
                    document: JSON.stringify({
                        boardData: null,
                        callType: 'Write',
                    }),
                },
            });
        },
        viewDetail: function (index, event) {
            const targetId = event.currentTarget.id;
            console.log(targetId);
            event.preventDefault(); //이벤트 전파를 차단하여 컴포넌트 이동에 지장이 없도록 하기 위함.

            this.$router.push({
                name: 'hello',
                params: { callType: 'detail', document: JSON.stringify(this.dataList[index]) },
            });
        },
        toggleBusy() {
            this.isBusy = !this.isBusy;
        },
        getList: async function (currentPage, countPerPage, params, docIdArr) {
            this.isBusy = true;
            let result = await this.$axios({
                method: 'post',
                url: '/api/getGeomBoardList',
                params: {
                    currentPage: currentPage,
                    countPerPage: countPerPage,
                    actionTarget: 'getList',
                    docIdArr: encodeURI(docIdArr),
                    params: params,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                    accesstoken: this.$store.state.token,
                },
            });

            if (result.status === 200) {
                this.dataList = result.data.list; //데이터 세팅
                this.pagination = result.data.pagination; //페이징 세팅

                this.toggleBusy(); //로딩 스피너 토글
            }
        },
    },
};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
h1 {
    color: #364a5f;
    font-weight: 300;
}
</style>
