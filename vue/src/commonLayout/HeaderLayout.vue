<!--
[HeaderLayout.vue 설명]
1. 공통 : 헤더 레이아웃

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
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <router-link class="navbar-brand" to="/">VueJS</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <!-- 
                    <li class="nav-item">
                        <router-link class="nav-link active nav-link" to="/" aria-current="page">Home</router-link>
                    </li>
                    -->
                    <li class="nav-item">
                        <router-link class="nav-link" to="/main">Main</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link class="nav-link" to="/auth">Auth</router-link>
                    </li>
                    <!-- 
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false"> Dropdown </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <li><a class="dropdown-item" href="#">Action</a></li>
                            <li><a class="dropdown-item" href="#">Another action</a></li>
                            <li><hr class="dropdown-divider" /></li>
                            <li><a class="dropdown-item" href="#">Something else here</a></li>
                        </ul>
                    </li> 
                    
                    <li class="nav-item">
                        <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">Disabled</a>
                    </li>
                    -->
                </ul>
                <form class="d-flex">
                    <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search" />
                    <button class="btn btn-outline-success" type="submit">Search</button>
                </form>

                <div class="btn-wrap navbar-nav mb-2 mb-lg-0" v-if="authYn == false">
                    <router-link class="btn login btn-outline-primary" to="/auth">Login</router-link>
                </div>
                <div class="btn-wrap navbar-nav mb-2 mb-lg-0" v-else-if="authYn == true">
                    <button class="btn login btn-outline-primary" @click="logout()">Logout</button>
                </div>
            </div>
        </div>
    </nav>
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {
    data() {
        let authYn1 = this.$store.getters.token == null ? false : true;
        return {
            authYn: authYn1,
        };
    },
    methods: {
        logout: async function () {
            this.$store.dispatch('logout'); // 로그인

            /*
            var result = await this.$axios({
                method: 'post',
                url: '/api/logout',
                params: {
                    callType: useParams.callType,
                },
                withCredentials: true,
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Access-Control-Allow-Origin': '*',
                },
            });
            */
        },
    },
};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
.navbar-brand {
    color: #364a5f;
}
.btn-wrap .login {
    margin-left: 5px;
}

@media (max-width: 991px) {
    .btn-wrap .login {
        margin: 5px 0px;
    }
}
</style>
