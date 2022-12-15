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
    <br />
    <!-- [data : 데이터 바인딩 지정] -->
    <b-row>
        <b-col md="8" offset-md="2">
            <div>
                <h1>{{ data }}</h1>
            </div>
            <br />
            <div>
                <!-- <b-button variant="danger">Button</b-button> -->
                <b-table hover :items="dataList"></b-table>
                <br />
                <b-table striped hover :items="dataList" :fields="dataFields"></b-table>
                <!-- <table hover>
                    <thead>
                        <tr>
                            <th>No</th>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>등록일시</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(row, idx) in dataList" :key="idx">
                            <td>{{ idx }}</td>
                            <td>
                                <a v-on:click="fnView(`${row.idx}`)">{{ row.brdwriter }}</a>
                            </td>
                            <td>{{ row.brdwriter }}</td>
                            <td>{{ row.brdwriter }}</td>
                        </tr>
                    </tbody>
                </table> -->
            </div>
            <br />
            <!-- [이미지 설정 실시] -->
            <div>
                <img src="../assets/logo.png" />
            </div>
        </b-col>
    </b-row>
    <hr />
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
            dataFields: [
                {
                    key: 'Brdno',
                    sortable: true,
                },
                {
                    key: 'Brdwriter',
                    sortable: false,
                },
                {
                    key: 'Brddate',
                    label: 'Person age',
                    sortable: true,
                    variant: 'danger',
                },
            ],
            dataList: [],
        };
    },
    mounted() {
        this.getList();
    },
    // [메소드 정의 실시]
    methods: {
        // [testMain 함수 정의 실시]
        testMain: function () {
            return 'hihihi';
        },
        getList: function () {
            this.$axios({
                method: 'get',
                url: '/api/getList',
                params: {
                    // callType: useParams.callType,
                    // targetId: useParams.targetId,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            })
                .then((res) => {
                    //console.log('응답 데이터 : ' + JSON.stringify(res.data));
                    this.dataList = res.data;
                })
                .catch((error) => {
                    console.log('에러 데이터 : ' + error.data);
                })
                .finally(() => {});
        },
    },
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
