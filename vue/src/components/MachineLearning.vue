<template>
    <div class="hello">
        <h1>{{ msg }}</h1>
        <p>Word2Ve과 FastText를 이용한 도로명주소 머신러닝 테스트 페이지 입니다.</p>
        <h4>주소를 입력 후 검색 해주세요.</h4>
        <div class="d-flex my-3">
            <select class="mx-1" v-model="leaningDataType">
                <option v-for="leaningDataType in leaningDataTypeArr" :key="leaningDataType" :value="leaningDataType">{{ leaningDataType }}</option>
            </select>
            <select class="mx-1" v-model="analyzerType">
                <option v-for="analyzerType in analyzerTypeArr" :key="analyzerType" :value="analyzerType">{{ analyzerType }}</option>
            </select>
            <select class="mx-1" v-model="refinementType">
                <option v-for="refinementType in refinementTypeArr" :key="refinementType" :value="refinementType[0]">{{ refinementType[1] }}</option>
            </select>
            <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search" v-model="searchKeyword" @keyup.enter="callgetAnalyzeKeyword()" />
            <button class="btn btn-outline-success" type="submit" @click="callgetAnalyzeKeyword()">Search</button>
        </div>
        <div id="app">
            <p>☆분석 결과☆</p>
            <ul>
                <li v-for="(item, index) in dataList" :key="index">{{ item }}</li>
            </ul>
        </div>
        <div id="app">
            <p>☆결과에 계산 추가☆</p>
            <ul>
                <li v-for="(item, index) in dataListLev" :key="index">{{ item }}</li>
            </ul>
        </div>
        <h3 class="my-3">1. Word2Vec(deeplearning4j)</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordWord2VecTrain('노원로28길', 'model', 'FULL')" value="Word2Vec 훈련 호출 - FULL" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordWord2VecTrain('노원로28길', 'model', 'ROAD')" value="Word2Vec 훈련 호출 - ROAD" />
            </li>
        </ul>
        <!-- 
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callgetAnalyzeKeyword()" value="Word2Vec 테스트 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordLeven()" value="레벤슈타인 단어 거리 분석 테스트 호출" />
            </li>
        </ul> 
        -->
        <h3 class="my-3">2. FastText(deeplearning4j)</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callTensorFlowFastTextTrain()" value="FastText 훈련 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callTensorFlowFastTextTest()" value="FastText 테스트 호출" />
            </li>
        </ul>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callTensorFlowJFastTextTrain()" value="JFastText 훈련 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callTensorFlowJFastTextTest()" value="JFastText 테스트 호출" />
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'HelloWorld',
    data() {
        return {
            isBusy: true,
            dataList: [],
            dataListLev: [],
            searchKeyword: '',
            leaningDataType: 'FULL',
            leaningDataTypeArr: ['FULL', 'ROAD'],
            analyzerType: 'Word2Vec',
            analyzerTypeArr: ['Word2Vec', 'FastText'],
            refinementType: 'All',
            refinementTypeArr: [
                ['All', '전체'],
                ['Road', '도로명'],
            ],
        };
    },
    props: {
        msg: String,
    },
    methods: {
        toggleBusy() {
            this.isBusy = !this.isBusy;
        },
        callGetAnalyzeKeywordWord2VecTrain: async function (keyword, analyzeType, leaningDataType) {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordWord2VecTrain',
                params: {
                    inputKeyword: keyword,
                    analyzeType: analyzeType,
                    leaningDataType: leaningDataType,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //this.id = result.data.id;
                console.log(result);
            }
        },
        callgetAnalyzeKeyword: async function () {
            var url = '';
            if (this.analyzerType == 'Word2Vec') {
                url = '/api/noAuth/getAnalyzeKeyword';
            } else {
                url = '/api/noAuth/getAnalyzeKeywordJFastTest';
            }

            const result = await this.$axios({
                method: 'GET',
                url: url,
                params: {
                    inputKeyword: this.searchKeyword,
                    analyzeType: 'model',
                    correctionYN: 'N',
                    leaningDataType: this.leaningDataType,
                    refinementType: this.refinementType,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                this.dataList = result.data.resuleMany; //데이터 세팅
                this.dataListLev = result.data.resuleManyLev;

                if (result.data.code == 'SUCESS03') {
                    alert('모델을 테스트 할 수 없는 환경에서 서버가 구동되었습니다');
                }

                this.toggleBusy(); //로딩 스피너 토글
            }
        },
        callGetAnalyzeKeywordLeven: async function () {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordLeven',
                params: {
                    inputKeyword: '노원로',
                    analyzeType: 'model',
                    correctionYN: 'N',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //console.log(result);
                if (result.data.code == 'SUCESS02') {
                    alert('분석 결과가 없습니다.');
                } else if (result.data.code == 'SUCESS03') {
                    alert('모델을 테스트 할 수 없는 환경에서 서버가 구동되었습니다');
                }
            }
        },
        callTensorFlowFastTextTrain: async function () {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordFastTextTrain',
                params: {
                    inputKeyword: '김해대로2431번길',
                    analyzeType: 'model',
                    correctionYN: 'N',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //this.id = result.data.id;
                console.log(result);
            }
        },
        callTensorFlowFastTextTest: async function () {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordFastTextTest',
                params: {
                    inputKeyword: '김해대로2325번길',
                    analyzeType: 'bin',
                    correctionYN: 'N',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //this.id = result.data.id;
                console.log(result);
            }
        },
        callTensorFlowJFastTextTrain: async function () {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordJFastTrain',
                params: {
                    inputKeyword: '김해대로2431번길',
                    analyzeType: 'model',
                    correctionYN: 'N',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //this.id = result.data.id;
                console.log(result);
            }
        },
        callTensorFlowJFastTextTest: async function () {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordJFastTest',
                params: {
                    inputKeyword: '김해대로2325번길',
                    analyzeType: 'bin',
                    correctionYN: 'N',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //this.id = result.data.id;
                console.log(result);
            }
        },
    },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
    margin: 40px 0 0;
}
ul {
    list-style-type: none;
    padding: 0;
}
li {
    display: inline-block;
    margin: 0 10px;
}
a {
    color: #42b983;
}
</style>
