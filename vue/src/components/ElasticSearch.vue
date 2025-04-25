<template>
    <div class="container my-5">
        <h1 class="text-center mb-4">{{ msg }}</h1>
        <p class="text-center lead">JAVA와 Elastic Search를 이용한 통합 검색 관리 페이지 입니다.</p>
        <h4 class="text-center mb-3">검색키워드를 입력해주세요.</h4>

        <!-- Search Form Section -->
        <div class="d-flex justify-content-center mb-5">
            <div class="input-group">
                <input class="form-control mx-1" type="search" placeholder="Search" aria-label="Search" v-model="searchKeyword" @keyup.enter="callgetAnalyzeKeyword()" />
                <button class="btn btn-outline-success mx-1" type="submit" @click="totalSearch()">Search</button>
            </div>
        </div>

        <!-- TF-IDF & Cosine Section -->
        <h5 class="mt-5">Python 모델 - Tf-Idf Cosine</h5>
        <ul class="list-unstyled my-3 btn-wrap">
            <li class="mb-2">
                <button class="btn btn-outline-success w-100" @click="makeSearchData()">검색 데이터 생성</button>
            </li>
            <li class="mb-2">
                <button class="btn btn-outline-success w-100" @click="deleteSearchData()">색인 데이터 삭제</button>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'HelloWorld',
    data() {
        return {
            searchKeyword: this.searchKeyword,
        };
    },
    props: {
        msg: String,
    },
    methods: {
        makeSearchData: function () {
            let result = this.$axios({
                method: 'get',
                url: '/api/search/setMakeSearchData',
                params: {
                    searchType: 'firebase',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                console.log(result);
            }
        },
        deleteSearchData: async function () {
            try {
                const response = await this.$axios({
                    method: 'get',
                    url: '/api/search/setDeleteSearchData',
                    params: {
                        indexName: 'geometry_board',
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                console.log(response, '★★★★');

                if (response.status === 200) {
                    const deleted = response.data.data.deleted; // ← 이 구조 중요!
                    console.log(deleted, '★★★★');

                    if (deleted === true) {
                        alert('geometry_board 색인의 삭제가 완료되었습니다.');
                    } else {
                        alert('geometry_board 색인이 존재하지 않습니다.cd');
                    }
                }
            } catch (error) {
                console.error('삭제 요청 실패:', error);
                alert('요청 처리 중 오류가 발생했습니다.');
            }
        },
        totalSearch: function () {
            let result = this.$axios({
                method: 'get',
                url: '/api/search/getSearchData',
                params: {
                    keyword: this.searchKeyword,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
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

ul.btn-wrap li {
    width: 45%;
}

.analysis_result_wrap .list-group-item {
    display: flex;
    font-size: 16px;
}

.analysis_result_wrap .list-group-item .index {
    width: calc(13% + 5px);
    text-align: center;
    line-height: 50px;
}

.analysis_result_wrap .list-group-item .contents {
    text-align: left;
    padding-left: 10px;
    line-height: 25px;
    /*margin: 0 auto;*/
}

@media (max-width: 990px) {
    .analysis_result_wrap .list-group-item {
        font-size: 14px;
    }
}

@media (max-width: 663px) {
    ul.btn-wrap li {
        width: 100%;
    }
}
</style>
