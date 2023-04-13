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
    <br /><br />
    <div class="wrapper fadeInDown" v-show="signIn">
        <div id="formContent">
            <!-- Icon -->
            <div class="fadeIn first">
                {{ msg }}
                <img src="../assets/logo.png" id="icon" alt="User Icon" />
            </div>

            <!-- Login Form -->
            <input type="text" id="login" class="fadeIn second" name="userId" v-model="user" placeholder="User Id" />
            <input type="text" id="password" class="fadeIn third" name="passwrod" v-model="password" placeholder="Password" />
            <input type="button" class="fadeIn fourth" value="Log In" @click="login()" />

            <!-- Remind Passowrd -->
            <div id="formFooter"><a class="underlineHover" href="#">Forgot Password?</a></div>
        </div>
    </div>
    <div class="wrapper fadeInDown" v-show="signUp">
        <div id="formContent">
            <!-- Icon -->
            <div class="fadeIn first">
                {{ msg }}
                <img src="../assets/logo.png" id="icon" alt="User Icon" />
            </div>

            <!-- Login Form -->

            <input type="text" id="userId" class="fadeIn second" name="userId" v-model="user" placeholder="User Id" @keyup="idValidation()" />
            <p v-if="idValidationMsg != ''">{{ idValidationMsg }}</p>
            <input type="text" id="password1" class="fadeIn third" name="password1" v-model="password" placeholder="Password1" />
            <input type="text" id="password2" class="fadeIn third" name="password2" v-model="Password2" placeholder="Password2" @keyup="pwValidation()" />
            <p v-if="pwValidationMsg != ''">{{ pwValidationMsg }}</p>
            <input type="text" id="userNm" class="fadeIn third" name="userNm" v-model="userNm" placeholder="userNm" />
            <input type="text" id="nickname" class="fadeIn third" name="nickname" v-model="nickname" placeholder="nickname" />
            <input type="text" id="mobile" class="fadeIn third" name="mobile" v-model="mobile" placeholder="mobile" />

            <input type="button" class="fadeIn fourth" value="Log In" @click="userRegistration()" />

            <!-- Remind Passowrd -->
            <div id="formFooter"><a class="underlineHover" href="#">Forgot Password?</a></div>
        </div>
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
        let accessType = this.$route.query.accessType == undefined || this.$route.query.accessType == 'SIGNIN' ? 'SIGNIN' : 'SIGNUP';
        let authYn = this.$store.getters.token == null ? false : true;
        console.log(accessType);
        console.log(authYn);

        let idValidationMsg = '';
        let pwValidationMsg = '';

        return {
            loginSuccess: false,
            loginError: false,
            user: '',
            password: '',
            password2: '',
            error: false,
            accessType: accessType, //접근 타입 1. SIGNUP 2. SIGNIN
            signIn: accessType == 'SIGNIN' ? true : false,
            signUp: accessType == 'SIGNUP' ? true : false,
            idValidationMsg: idValidationMsg,
            pwValidationMsg: pwValidationMsg,
        };
    },
    mounted() {},
    // [메소드 정의 실시]
    methods: {
        getLogin: async function () {
            await this.$axios({
                method: 'get',
                url: '/api/getList',
                params: {
                    // callType: useParams.callType,
                    // targetId: useParams.targetId,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
        },
        login: async function () {
            // 아이디와 패스워드 입력여부 확인
            if (this.user && this.password) {
                var id = this.user; // 아이디
                var password = this.password; // 비밀번호

                console.log(id);
                console.log(password);
                this.$store.dispatch('login', { id, password }); // 로그인
                //console.log(this.$store);
            } else {
                alert('아이디 또는 비밀번호가 입력되지 않았습니다.');
                return false;
            }
        },
        loginBak: async function () {
            try {
                const result = await this.$axios.get('/api/login', {
                    auth: {
                        username: this.user,
                        password: this.password,
                    },
                });

                if (result.status === 200) {
                    this.loginSuccess = true;
                }
            } catch (err) {
                this.loginError = true;
                throw new Error(err);
            }

            console.log('-------------------');
            console.log(this.user);
            console.log(this.password);
            console.log(this.loginError);
            console.log(this.loginSuccess);
            console.log('-------------------');
        },
        idValidation: async function () {
            var id = this.user; // 아이디
            var password = this.password; // 비밀번호

            console.log(id);
            console.log(password);

            if (id == '') {
                this.idValidationMsg = '';
                return false;
            }

            let res = await this.$axios({
                method: 'post',
                url: '/api/idValidation',
                params: {
                    email: id,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (res.data == '0') {
                this.idValidationMsg = '사용하실 수 있는 아이디입니다.';
            } else {
                this.idValidationMsg = '이미 가입되어 있는 아이디입니다.';
            }
        },
        pwValidation: async function () {
            alert('하위');
            var id = this.user; // 아이디
            var password = this.password; // 비밀번호
            var password2 = this.password2; // 비밀번호 화인

            console.log(id);
            console.log(password);
            console.log(password2);

            if (password2 == '') {
                this.pwValidationMsg = '';
                return false;
            }

            if (password == password2) {
                this.pwValidationMsg = '두개의 비밀번호가 일치 합니다.';
            } else {
                this.pwValidationMsg = '두개의 비밀번호가 일치하지 않습니다.';
            }
        },
        userRegistration: async function () {
            let res = await this.$axios({
                method: 'post',
                url: '/api/userRegistration',
                params: {
                    email: this.user,
                    password: this.password,
                    name: this.userNm,
                    nickname: this.nickname,
                    mobile: this.mobile,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            console.log(res);
        },
    },
};
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
html {
    background-color: #56baed;
}

body {
    font-family: 'Poppins', sans-serif;
    height: 100vh;
}

a {
    color: #92badd;
    display: inline-block;
    text-decoration: none;
    font-weight: 400;
}

h2 {
    text-align: center;
    font-size: 16px;
    font-weight: 600;
    text-transform: uppercase;
    display: inline-block;
    margin: 40px 8px 10px 8px;
    color: #cccccc;
}

/* STRUCTURE */

.wrapper {
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    width: 100%;
    min-height: 100%;
    padding: 20px;
}

#formContent {
    -webkit-border-radius: 10px 10px 10px 10px;
    border-radius: 10px 10px 10px 10px;
    background: #fff;
    padding: 30px;
    width: 90%;
    /* max-width: 450px; */
    position: relative;
    padding: 0px;
    -webkit-box-shadow: 0 30px 60px 0 rgba(0, 0, 0, 0.3);
    box-shadow: 0 30px 60px 0 rgba(0, 0, 0, 0.3);
    text-align: center;
}

#formFooter {
    background-color: #f6f6f6;
    border-top: 1px solid #dce8f1;
    padding: 25px;
    text-align: center;
    -webkit-border-radius: 0 0 10px 10px;
    border-radius: 0 0 10px 10px;
}

/* TABS */

h2.inactive {
    color: #cccccc;
}

h2.active {
    color: #0d0d0d;
    border-bottom: 2px solid #5fbae9;
}

/* FORM TYPOGRAPHY*/

input[type='button'],
input[type='submit'],
input[type='reset'] {
    background-color: #364a5f;
    border: none;
    color: white;
    padding: 15px 80px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    text-transform: uppercase;
    font-size: 13px;
    -webkit-box-shadow: 0 10px 30px 0 rgba(95, 186, 233, 0.4);
    box-shadow: 0 10px 30px 0 rgba(95, 186, 233, 0.4);
    -webkit-border-radius: 5px 5px 5px 5px;
    border-radius: 5px 5px 5px 5px;
    margin: 5px 20px 40px 20px;
    -webkit-transition: all 0.3s ease-in-out;
    -moz-transition: all 0.3s ease-in-out;
    -ms-transition: all 0.3s ease-in-out;
    -o-transition: all 0.3s ease-in-out;
    transition: all 0.3s ease-in-out;
}

input[type='button']:hover,
input[type='submit']:hover,
input[type='reset']:hover {
    background-color: #42b883;
}

input[type='button']:active,
input[type='submit']:active,
input[type='reset']:active {
    -moz-transform: scale(0.95);
    -webkit-transform: scale(0.95);
    -o-transform: scale(0.95);
    -ms-transform: scale(0.95);
    transform: scale(0.95);
}

input[type='text'] {
    background-color: #f6f6f6;
    border: none;
    color: #0d0d0d;
    padding: 15px 32px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 5px;
    width: 85%;
    border: 2px solid #f6f6f6;
    -webkit-transition: all 0.5s ease-in-out;
    -moz-transition: all 0.5s ease-in-out;
    -ms-transition: all 0.5s ease-in-out;
    -o-transition: all 0.5s ease-in-out;
    transition: all 0.5s ease-in-out;
    -webkit-border-radius: 5px 5px 5px 5px;
    border-radius: 5px 5px 5px 5px;
}

input[type='text']:focus {
    background-color: #fff;
    border-bottom: 1px solid #42b883;
}

input[type='text']:placeholder {
    color: #cccccc;
}

/* ANIMATIONS */

/* Simple CSS3 Fade-in-down Animation */
.fadeInDown {
    -webkit-animation-name: fadeInDown;
    animation-name: fadeInDown;
    -webkit-animation-duration: 1s;
    animation-duration: 1s;
    -webkit-animation-fill-mode: both;
    animation-fill-mode: both;
}

@-webkit-keyframes fadeInDown {
    0% {
        opacity: 0;
        -webkit-transform: translate3d(0, -100%, 0);
        transform: translate3d(0, -100%, 0);
    }
    100% {
        opacity: 1;
        -webkit-transform: none;
        transform: none;
    }
}

@keyframes fadeInDown {
    0% {
        opacity: 0;
        -webkit-transform: translate3d(0, -100%, 0);
        transform: translate3d(0, -100%, 0);
    }
    100% {
        opacity: 1;
        -webkit-transform: none;
        transform: none;
    }
}

/* Simple CSS3 Fade-in Animation */
@-webkit-keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}
@-moz-keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}
@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

.fadeIn {
    opacity: 0;
    -webkit-animation: fadeIn ease-in 1;
    -moz-animation: fadeIn ease-in 1;
    animation: fadeIn ease-in 1;

    -webkit-animation-fill-mode: forwards;
    -moz-animation-fill-mode: forwards;
    animation-fill-mode: forwards;

    -webkit-animation-duration: 1s;
    -moz-animation-duration: 1s;
    animation-duration: 1s;
}

.fadeIn.first {
    -webkit-animation-delay: 0.4s;
    -moz-animation-delay: 0.4s;
    animation-delay: 0.4s;
}

.fadeIn.second {
    -webkit-animation-delay: 0.6s;
    -moz-animation-delay: 0.6s;
    animation-delay: 0.6s;
}

.fadeIn.third {
    -webkit-animation-delay: 0.8s;
    -moz-animation-delay: 0.8s;
    animation-delay: 0.8s;
}

.fadeIn.fourth {
    -webkit-animation-delay: 1s;
    -moz-animation-delay: 1s;
    animation-delay: 1s;
}

/* Simple CSS3 Fade-in Animation */
.underlineHover:after {
    display: block;
    left: 0;
    bottom: -10px;
    width: 0;
    height: 2px;
    background-color: #56baed;
    content: '';
    transition: width 0.2s;
}

.underlineHover:hover {
    color: #0d0d0d;
}

.underlineHover:hover:after {
    width: 100%;
}

/* OTHERS */

*:focus {
    outline: none;
}

#icon {
    width: 20%;
}
</style>
