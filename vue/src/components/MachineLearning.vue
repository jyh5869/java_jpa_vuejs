<!-- 
<template>
    <div class="hello">
        <h1>{{ msg }}</h1>
        <p>Word2Ve과 FastText를 이용한 도로명주소 머신러닝 테스트 페이지 입니다.</p>
        <h4>주소를 입력 후 검색 해주세요.</h4>
        <div class="d-flex my-3">
            <select class="mx-1" v-model="analyzerType" v-if="analyzerTypeView == true" @change="changeAnalyzerType">
                <option v-for="analyzerType in analyzerTypeArr" :key="analyzerType" :value="analyzerType">{{ analyzerType }}</option>
            </select>
            <select class="mx-1" v-model="leaningDataType" v-if="leaningDataTypeView == true">
                <option v-for="leaningDataType in leaningDataTypeArr" :key="leaningDataType" :value="leaningDataType">{{ leaningDataType }}</option>
            </select>
            <select class="mx-1" v-model="refinementType" v-if="refinementTypeView == true">
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
        <h3 class="my-3">1-1 JAVA를 활용한 분석 모델(Word2Vec[deeplearning4j])</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordWord2VecTrain('노원로28길', 'model', 'FULL')" value="Word2Vec 훈련 호출 - FULL" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordWord2VecTrain('노원로28길', 'model', 'ROAD')" value="Word2Vec 훈련 호출 - ROAD" />
            </li>
        </ul>
        <h3 class="my-3">1-2 JAVA를 활용한 분석 모델(FastText[deeplearning4j])</h3>
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
        <h3 class="my-3">2-1. Python을 활용한 분석 모델(TF-IDF AND COSINE)</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="tfIdfAndCosineRadaModelMake()" value="파이썬 훈련 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="tfIdfAndCosineRadaModelUse()" value="파이썬 테스트 호출" />
            </li>
        </ul>
        <h3 class="my-3">2-2. Python을 활용한 분석 모델(KERAS)</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="kerasModelMake()" value="파이썬 훈련 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="kerasModelUse()" value="파이썬 테스트 호출" />
            </li>
        </ul>
    </div>
</template>
-->

<template>
    <div class="container my-5">
        <h1 class="text-center mb-4">{{ msg }}</h1>
        <p class="text-center lead">JAVA와 Python을 이용한 도로명 머신러닝 테스트 페이지입니다.</p>
        <h4 class="text-center mb-3">도로명을 입력 후 검색해주세요.</h4>

        <!-- Search Form Section -->
        <div class="d-flex justify-content-center mb-5">
            <div class="input-group">
                <select class="form-select mx-1" v-model="analyzerType" v-if="analyzerTypeView == true" @change="changeAnalyzerType">
                    <option v-for="analyzerType in analyzerTypeArr" :key="analyzerType" :value="analyzerType">{{ analyzerType }}</option>
                </select>
                <select class="form-select mx-1" v-model="leaningDataType" v-if="leaningDataTypeView == true">
                    <option v-for="leaningDataType in leaningDataTypeArr" :key="leaningDataType" :value="leaningDataType">{{ leaningDataType }}</option>
                </select>
                <select class="form-select mx-1" v-model="refinementType" v-if="refinementTypeView == true">
                    <option v-for="refinementType in refinementTypeArr" :key="refinementType" :value="refinementType[0]">{{ refinementType[1] }}</option>
                </select>
                <input class="form-control mx-1" type="search" placeholder="Search" aria-label="Search" v-model="searchKeyword" @keyup.enter="callgetAnalyzeKeyword()" />
                <button class="btn btn-outline-success mx-1" type="submit" @click="callgetAnalyzeKeyword()">Search</button>
            </div>
        </div>

        <!-- Results Section -->
        <div class="row">
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-body py-1">
                        <h5 class="card-title pt-2">분석결과</h5>
                        <ul class="list-group">
                            <li class="list-group-item" v-for="(item, index) in dataList" :key="index">{{ item }}</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-body py-1">
                        <h5 class="card-title pt-2">리벤슈타인 계산결과</h5>
                        <ul class="list-group">
                            <li class="list-group-item" v-for="(item, index) in dataListLev" :key="index">{{ item }}</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Word2Vec Section -->
        <h5 class="mt-4">Java 모델 - Word2Vec(Deeplearning4j)</h5>
        <ul class="list-unstyled my-3 btn-wrap">
            <li class="mb-2 w-40">
                <button class="btn btn-outline-primary btn-block w-100 p-2" @click="callGetAnalyzeKeywordWord2VecTrain('노원로28길', 'model', 'FULL')">Word2Vec 훈련 호출 - FULL</button>
            </li>
            <li class="mb-2 w-40">
                <button class="btn btn-outline-primary btn-block w-100 p-2" @click="callGetAnalyzeKeywordWord2VecTrain('노원로28길', 'model', 'ROAD')">Word2Vec 훈련 호출 - ROAD</button>
            </li>
        </ul>

        <!-- FastText Section -->
        <h5 class="mt-5">Java 모델 - FastText(Deeplearning4j)</h5>
        <ul class="list-unstyled my-3 btn-wrap">
            <li class="mb-2">
                <button class="btn btn-outline-primary w-100" @click="callTensorFlowFastTextTrain()">FastText 훈련 호출</button>
            </li>
            <li class="mb-2">
                <button class="btn btn-outline-primary w-100" @click="callTensorFlowFastTextTest()">FastText 테스트 호출</button>
            </li>
        </ul>
        <ul class="list-unstyled my-3 btn-wrap">
            <li class="mb-2">
                <button class="btn btn-outline-primary w-100" @click="callTensorFlowJFastTextTrain()">JFastText 훈련 호출</button>
            </li>
            <li class="mb-2">
                <button class="btn btn-outline-primary w-100" @click="callTensorFlowJFastTextTest()">JFastText 테스트 호출</button>
            </li>
        </ul>

        <!-- TF-IDF & Cosine Section -->
        <h5 class="mt-5">Python 모델 - Tf-Idf Cosine</h5>
        <ul class="list-unstyled my-3 btn-wrap">
            <li class="mb-2">
                <button class="btn btn-outline-success w-100" @click="tfIdfAndCosineRadaModelMake()">Tf-Idf Cosine 훈련 호출</button>
            </li>
            <li class="mb-2">
                <button class="btn btn-outline-success w-100" @click="tfIdfAndCosineRadaModelUse()">Tf-Idf Cosine 테스트 호출</button>
            </li>
        </ul>

        <!-- Keras Section -->
        <h5 class="mt-5">Python 모델 - Keras</h5>
        <ul class="list-unstyled my-3 btn-wrap">
            <li class="mb-2">
                <button class="btn btn-outline-success w-100" @click="kerasModelMake()">Keras 훈련 호출</button>
            </li>
            <li class="mb-2">
                <button class="btn btn-outline-success w-100" @click="kerasModelUse()">Keras 테스트 호출</button>
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
            defaultKeyword: '노원로',
            leaningDataType: 'FULL',
            leaningDataTypeView: false,
            leaningDataTypeArr: ['FULL', 'ROAD'],
            analyzerType: 'Keras',
            analyzerTypeView: true,
            analyzerTypeArr: ['Word2Vec', 'FastText', 'Cosign', 'Keras'],
            refinementType: 'All',
            refinementTypeView: false,
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
        changeAnalyzerType: async function () {
            if (this.analyzerType == 'FastText' || this.analyzerType == 'Word2Vec') {
                this.leaningDataTypeView = true;
                this.refinementTypeView = true;
            } else {
                this.refinementTypeView = false;
                this.leaningDataTypeView = false;
            }
        },
        checkForm: async function () {
            let searchKeyword = this.searchKeyword;
            if (searchKeyword == '' || searchKeyword == null) {
                let confirmYn = confirm("키워드가 입력되지 않았습니다.\n기본키워드인 '" + this.defaultKeyword + "'로 훈련 및 호출 하시겠습니까?");
                console.log(confirmYn + ' ');
                if (confirmYn == true) {
                    this.searchKeyword = this.defaultKeyword;
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        },
        callgetAnalyzeKeyword: async function () {
            var url = '/api/noAuth/tfIdfAndCosineRadaModelUse'; //기본 URL - TF-IDF-COSINE

            if (this.analyzerType == 'Word2Vec') {
                url = '/api/noAuth/getAnalyzeKeyword'; //Word2Vec Model
            } else if (this.analyzerType == 'FastText') {
                url = '/api/noAuth/getAnalyzeKeywordJFastTest'; //FastText Model
            } else if (this.analyzerType == 'Cosine') {
                url = '/api/noAuth/tfIdfAndCosineRadaModelUse'; //Cosine Model
            } else if (this.analyzerType == 'Keras') {
                url = '/api/noAuth/kerasModelUse'; //Keras Model
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
                this.dataList = result.data.resultMany; //데이터 세팅
                this.dataListLev = result.data.resultManyLev;

                if (result.data.code == 'SUCESS03') {
                    alert('모델을 테스트 할 수 없는 환경에서 서버가 구동되었습니다');
                }

                this.toggleBusy(); //로딩 스피너 토글
            }
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
                    inputKeyword: '김해대로',
                    analyzeType: 'vec',
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
        tfIdfAndCosineRadaModelMake: async function () {
            if ((await this.checkForm()) == true) {
                const result = await this.$axios({
                    method: 'GET',
                    url: '/api/noAuth/tfIdfAndCosineRadaModelMake',
                    params: {
                        inputKeyword: this.searchKeyword,
                        analyzeType: 'model',
                        correctionYN: 'N',
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                if (result.status === 200) {
                    if (result.data.code == 'SUCESS01') {
                        alert('Tf-Idf-Cosine 모델 훈련을 완료 했습니다.');
                    } else if (result.data.code == 'FAIL01') {
                        alert('Tf-Idf-Cosine 모델을 훈련을 실패했습니다.');
                    } else {
                        alert('Tf-Idf-Cosine 모델 훈련 중 알수없는 문제가 발생했습니다.');
                    }
                }
            }
        },
        tfIdfAndCosineRadaModelUse: async function () {
            if ((await this.checkForm()) == true) {
                const result = await this.$axios({
                    method: 'GET',
                    url: '/api/noAuth/tfIdfAndCosineRadaModelUse',
                    params: {
                        inputKeyword: this.searchKeyword,
                        analyzeType: 'vec',
                        correctionYN: 'N',
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                if (result.status === 200) {
                    if (result.data.code == 'SUCESS01') {
                        this.dataList = result.data.resultMany;
                        this.dataListLev = result.data.resultManyLev;
                    } else if (result.data.code == 'FAIL01') {
                        alert('Tf-Idf-Cosine 모델 호출에 실패했습니다.');
                    } else {
                        alert('Tf-Idf-Cosine 모델 호출 중 알수없는 문제가 발생했습니다.');
                    }
                }
            }
        },
        kerasModelMake: async function () {
            if ((await this.checkForm()) == true) {
                const result = await this.$axios({
                    method: 'GET',
                    url: '/api/noAuth/kerasModelMake',
                    params: {
                        inputKeyword: this.searchKeyword,
                        analyzeType: 'bin',
                        correctionYN: 'N',
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                if (result.status === 200) {
                    if (result.data.code == 'SUCESS01') {
                        alert('Keras 모델 훈련을 완료 했습니다.');
                    } else if (result.data.code == 'FAIL01') {
                        alert('Keras 모델을 훈련을 실패했습니다.');
                    } else {
                        alert('Keras 모델 훈련 중 알수없는 문제가 발생했습니다.');
                    }
                }
            }
        },
        kerasModelUse: async function () {
            if ((await this.checkForm()) == true) {
                const result = await this.$axios({
                    method: 'GET',
                    url: '/api/noAuth/kerasModelUse',
                    params: {
                        inputKeyword: this.searchKeyword,
                        analyzeType: 'bin',
                        correctionYN: 'N',
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                if (result.status === 200) {
                    if (result.data.code == 'SUCESS01') {
                        this.dataList = result.data.resultMany;
                        this.dataListLev = result.data.resultManyLev;
                    } else if (result.data.code == 'FAIL01') {
                        alert('Keras 모델 호출에 실패했습니다.');
                    } else {
                        alert('Keras 모델 호출 중 알수없는 문제가 발생했습니다.');
                    }
                }
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

ul.btn-wrap li {
    width: 45%;
}
@media (max-width: 663px) {
    ul.btn-wrap li {
        width: 100%;
    }
}
</style>
