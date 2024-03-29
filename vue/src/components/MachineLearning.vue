<template>
    <div class="hello">
        <h1>{{ msg }}</h1>
        <p>Word2Ve과 Tensorflow를 이용한 도로명주소 머신러닝 테스트 페이지 입니다.</p>
        <h3>1. Word2Vec(deeplearning4j)</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordWord2VecTrain()" value="Word2Vec 훈련 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callgetAnalyzeKeyword()" value="Word2Vec 테스트 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callGetAnalyzeKeywordLeven()" value="레벤슈타인 단어 거리 분석 테스트 호출" />
            </li>
        </ul>
        <h3>1. FastText(deeplearning4j)</h3>
        <ul>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callTensorFlowFastTextTrain()" value="FastText 훈련 호출" />
            </li>
            <li>
                <input type="button" class="fadeIn fourth small" @click="callTensorFlowFastTextTest()" value="FastText 테스트 호출" />
            </li>
        </ul>
        <h3>2. TensorFlow</h3>
        <ul></ul>
    </div>
</template>

<script>
export default {
    name: 'HelloWorld',
    props: {
        msg: String,
    },
    methods: {
        callGetAnalyzeKeywordWord2VecTrain: async function () {
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeywordWord2VecTrain',
                params: { inputKeyword: '노원로' },
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
            const result = await this.$axios({
                method: 'GET',
                url: '/api/noAuth/getAnalyzeKeyword',
                params: {
                    inputKeyword: '노원로28길',
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
                //this.id = result.data.id;
                console.log(result);
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
