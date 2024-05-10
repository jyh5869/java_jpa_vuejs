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
            <div id="formFooter"><a class="underlineHover" href="javascript:void('0');" @click="sendAuthEmail('changeInfo')">Forgot Password?!</a></div>
        </div>
    </div>
    <div class="wrapper fadeInDown" v-show="signUpAndModify">
        <div id="formContent">
            <!-- Icon -->
            <div class="fadeIn first">
                {{ msg }}
                <img src="../assets/logo.png" id="icon" alt="User Icon" />
            </div>

            <!-- Login Form -->
            <input type="text" id="userId" class="fadeIn second" name="userId" v-model="user" placeholder="User Id" @keyup="idValidation()" :disabled="accessType == 'MODIFY'" />
            <p class="text-success sm" v-if="idValidationMsg != ''">{{ idValidationMsg }}</p>

            <div v-if="accessType == 'SIGNUP'">
                <!-- 이메일인증 전 -->
                <div v-if="emailAuthYN == false">
                    <input type="button" class="fadeIn fourth small" @click="sendAuthEmail('emailAuth')" value="이메일 인증하기" />
                    <div v-if="emailSendYN == true">
                        <input type="text" id="inputEmailAuthCd" class="fadeIn fourth small" placeholder="코드를 입력해 주세요." />
                        <input type="button" class="fadeIn fourth small" value="인증코드 확인" @click="emailAuthYn()" />
                    </div>
                </div>
                <!-- 이메일인증 후 -->
                <div v-if="emailAuthYN == true">
                    <p class="text-success sm">이메일 인증이 완료되었습니다!</p>
                </div>
            </div>
            <input type="text" id="password1" class="fadeIn third" name="password1" v-model="password" placeholder="Password1" @keyup="pwValidation()" />
            <input type="text" id="password2" class="fadeIn third" name="password2" v-model="password2" placeholder="Password2" @keyup="pwValidation()" />
            <p class="text sm" v-if="pwValidationMsg != ''">{{ pwValidationMsg }}</p>

            <input type="text" id="address" class="fadeIn third" name="address" v-model="address" placeholder="Address" />
            <input type="button" class="fadeIn fourth small" @click="searchAddress(address)" value="주소찾기" />

            <input type="text" id="name" class="fadeIn third" name="name" v-model="name" placeholder="name" />
            <input type="text" id="nickname" class="fadeIn third" name="nickname" v-model="nickname" placeholder="nickname" />
            <input type="text" id="mobile" class="fadeIn third" name="mobile" v-model="mobile" placeholder="mobile" />

            <div class="mx-0">
                <input type="button" class="fadeIn fourth" value="Information Change(Login)" v-if="accessPath == 'login'" @click="userManagement()" />
                <input type="button" class="fadeIn fourth" value="Information Change(EmailAuth)" v-if="accessPath == 'emailAuth'" @click="userManagementAuthEmail('MODIFY')" />
            </div>

            <!-- Remind Passowrd -->
            <!-- <div id="formFooter"><a class="underlineHover" href="javascript:void(0);">Forgot Password?</a></div> -->
            <div id="formFooter" v-if="accessType == 'MODIFY'">
                <a class="underlineHover" href="javascript:void(0);" v-if="accessPath == 'login'" @click="userManagement('DELETE')">Delete account(Login)</a>
                <a class="underlineHover" href="javascript:void(0);" v-if="accessPath == 'emailAuth'" @click="userManagementAuthEmail('DELETE')">Delete account(EmailAuth)</a>
            </div>
        </div>
    </div>
    <div class="addrSearch-wrap">
        <div class="addrSearchResult-wrap" v-if="addrSearchView == true">
            <div class="row">
                <div class="col text-end"><b-close-button @click="setSelectAddress('')" /></div>
            </div>
            <div class="row">
                <div class="col">
                    <h3 class="x-lg">주소를 입력해 주세요</h3>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <input type="text" class="" placeholder="Search Address" v-model="keyword" @keyup.enter="searchAddress(keyword)" />
                </div>
            </div>
            <div class="row mt-3">
                <div class="col">
                    <b-list-group class="searchResult-wrap">
                        <b-list-group-item variant="secondary" href="#" v-for="item in jsonData" :key="item.bdMgtSn" @click="setSelectAddress(item.roadAddr)" class="searchResultRow">{{ item.roadAddr }}</b-list-group-item>
                    </b-list-group>
                </div>
            </div>
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
        let accessType = this.$route.query.accessType == undefined ? 'SIGNIN' : this.$route.query.accessType;
        let accessPath = this.$route.query.accessPath == 'emailAuth' ? 'emailAuth' : 'login';
        let authYn = this.$store.getters.token == null ? false : true;

        //회원정보 수정일 경우 호출
        if (accessType == 'MODIFY') {
            /*
                1. 비밀번호 분실로 이메일 인증을통해 토큰을 가지고 접근한 경우
                2. 로그인 후 정보변경을 위해 접근한경우
            */
            //if (this.$route.query.token != undefined && this.$route.query.token != null) {
            if (accessPath == 'emailAuth') {
                let id = this.$route.query.id;
                let token = this.$route.query.token;

                this.getUserInfoAuthEmail(id, token);
            } else {
                let id = this.$store.getters.id;

                this.getUserInfo(id);
            }
        }

        let idValidationMsg = '';
        let pwValidationMsg = '';

        return {
            loginSuccess: false,
            loginError: false,
            authYn: authYn,
            id: '',
            user: '',
            password: '',
            password2: '',
            name: '',
            mobile: '',
            nickname: '',
            address: '',
            profile: '',
            deleteYn: '',
            authType: this.$store.getters.authType,
            error: false,
            accessType: accessType, //접근 타입 1. SIGNUP 2. SIGNIN
            accessPath: accessPath, //인증 타입 1. Login 2. EmailAuth
            signIn: accessType == 'SIGNIN' ? true : false,
            signUpAndModify: accessType == 'SIGNUP' || accessType == 'MODIFY' ? true : false,
            idValidationFlag: accessType == 'SIGNUP' ? false : true,
            pwValidationFlag: false,
            idValidationMsg: idValidationMsg,
            pwValidationMsg: pwValidationMsg,
            emailSendYN: false,
            emailAuthCd: '',
            emailAuthYN: false,
            jsonData: [],
            dataList: [],
            dataListLev: [],
            addrSearchView: false,
        };
    },
    mounted() {},
    // [메소드 정의 실시] https://codesandbox.io/examples/package/vue3-openlayers
    methods: {
        login: async function () {
            // 아이디와 패스워드 입력여부 확인
            if (this.user && this.password) {
                var id = this.user; // 아이디
                var password = this.password; // 비밀번호

                this.$store.dispatch('login', { id, password }); // 로그인
                this.$emit('data-to-parent', { toast: { title: '로그인 성공', body: id + '님 환영합니다.', variant: 'success' } });
            } else {
                alert('아이디 또는 비밀번호가 입력되지 않았습니다.\n 확인 후 다시 시도해 주세요.');
                return false;
            }
        },
        idValidation: async function () {
            var id = this.user; // 아이디

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
                this.idValidationFlag = true;
            } else {
                this.idValidationMsg = '이미 가입되어 있는 아이디입니다.';
                this.idValidationFlag = false;
            }
        },
        pwValidation: async function () {
            // 비밀번호 일치 검증
            var password1 = this.password;
            var password2 = this.password2;

            if (password1 == '' || password2 == '') {
                this.pwValidationMsg = '';
                return false;
            }

            if (password1 == password2) {
                this.pwValidationMsg = '두개의 비밀번호가 일치 합니다.';
                this.pwValidationFlag = true;
                return true;
            } else {
                this.pwValidationMsg = '두개의 비밀번호가 일치하지 않습니다.';
                this.pwValidationFlag = false;
                return false;
            }
        },
        formValidation: async function () {
            let arrParams = [this.user, this.password, this.password2, this.name, this.nickname, this.mobile, this.address];
            let arrFields = ['아이디', '비밀번호', '비밀번호 확인', '이름', '닉네임', '휴대폰 번호', '주소'];
            let arrValidParams = [this.idValidationFlag, this.pwValidationFlag];
            let arrValidMsg = ['아이디 중복', '비밀번호 일치 여부'];

            //폼값 체크1
            for (var i in arrParams) {
                if (arrParams[i] === '') {
                    let feildNm = arrFields[i];
                    alert(feildNm + '을(를) 입력해 주세요.');
                    return false;
                }
            }

            //폼값체크2
            for (var j in arrValidParams) {
                if (arrValidParams[j] === false) {
                    let validNm = arrValidMsg[j];
                    alert(validNm + '을(를) 체크해 주세요.');
                    return false;
                }
            }

            //회원가입의 경후 이메일 인증을 체크한다.
            if (this.accessType == 'SIGNUP' && this.emailAuthYN == false) {
                alert('가입을 위한 이메일 인증을 완료해 주세요.');
                return false;
            }

            return true;
        },
        userManagement: async function (type) {
            if (this.accessType == 'SIGNUP' && type != 'DELETE') {
                console.log('회원가입 진행');

                //1.가입형식 체크 및 이메일 인증여부 체크
                if ((await this.formValidation()) == false) {
                    return false;
                }

                let result = await this.$axios({
                    method: 'post',
                    url: '/api/userRegistration',
                    params: {
                        email: this.user,
                        password: this.password,
                        name: this.name,
                        mobile: this.mobile,
                        nickname: this.nickname,
                        address: this.address,
                        profile: this.profile,
                        authType: 'user',
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                if (result.status === 200) {
                    this.signIn = true;
                    this.signUpAndModify = false;
                }
            } else if (this.accessType == 'MODIFY' && type != 'DELETE') {
                console.log('회원정보 수정 : ' + this.authType);

                if ((await this.formValidation()) == false) {
                    return false;
                }

                //패스워드 일치확인 검증
                if ((await this.pwValidation()) === false) {
                    this.pwValidationMsg = '비밀번호화 비밀번호 확인을 입력 해주세요.';
                    return false;
                }

                let result = await this.$axios({
                    method: 'post',
                    url: '/api/userModify',
                    params: {
                        actionType: 'UPDATE',
                        id: this.id,
                        email: this.user,
                        password: this.password,
                        name: this.name,
                        nickname: this.nickname,
                        mobile: this.mobile,
                        profile: this.profile,
                        address: this.address,
                        deleteYn: this.deleteYn,
                        authType: this.authType,
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        accesstoken: this.$store.state.token,
                    },
                });

                let actionType = result.data.actionType;
                let returnFlag = result.data.returnFlag;
                let updateCnt = result.data.updateCnt;
                let errorCode = result.data.errorCode;
                let errorMsg;

                if (result.status === 200 && returnFlag == true) {
                    errorMsg = '업데이트(' + actionType + ') 성공  Communication Code = ' + result.status + ' (' + updateCnt + '건)';
                    alert(errorMsg);

                    //통신이 성공적이고 변경 건수가 0이 아닌 경우 메인으로 이동
                    this.$router.push({
                        name: 'BoardList',
                    });
                } else {
                    errorMsg = '업데이트(' + actionType + ') 실패  Communication Code = ' + result.status + '\n';

                    if (errorCode == 'ERROR01') {
                        errorMsg += errorCode + ' - 비밀번호 불일치, 비밀번호를 확인후 다시 시도해주세요. ';
                    } else if (errorCode == 'ERROR02') {
                        errorMsg += errorCode + ' - 수정 처리중 오류 발생, 잠시 후 다시 시도해주세요.';
                    } else {
                        errorMsg += ' - 알수 없는 오류 발생 잠시 후 다시 시도해주세요.';
                    }

                    alert(errorMsg);
                }
            } else if (type == 'DELETE') {
                //패스워드 검증
                if ((await this.pwValidation()) === false) {
                    this.pwValidationMsg = '비밀번호화 비밀번호 확인을 입력 해주세요.';
                    return false;
                }

                if (confirm('계정을 삭제 하시겠습니까?')) {
                    let result = await this.$axios({
                        method: 'post',
                        url: '/api/userModify',
                        params: {
                            actionType: 'DELETE',
                            id: this.id,
                            email: this.user,
                            password: this.password,
                            name: this.name,
                            nickname: this.nickname,
                            mobile: this.mobile,
                            profile: this.profile,
                            address: this.address,
                        },
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            accesstoken: this.$store.state.token,
                        },
                    });
                    let actionType = result.data.actionType;
                    let returnFlag = result.data.returnFlag;
                    let updateCnt = result.data.updateCnt;
                    let errorCode = result.data.errorCode;
                    let errorMsg;

                    if (result.status === 200 && returnFlag == true) {
                        errorMsg = '삭제(' + actionType + ') 성공  Communication Code = ' + result.status + ' (' + updateCnt + '건)';
                        alert(errorMsg);

                        //통신이 성공적이고 변경 건수가 0이 아닌 경우 로그아웃 처리 후 메인으로 이동
                        this.$store.dispatch('logout');
                    } else {
                        errorMsg = '삭제(' + actionType + ') 실패! Communication Code = ' + result.status + '\n';

                        if (errorCode == 'ERROR01') {
                            errorMsg += errorCode + ' - 비밀번호 불일치, 비밀번호를 확인후 다시 시도해주세요. ';
                        } else if (errorCode == 'ERROR02') {
                            errorMsg += errorCode + ' - 삭제 처리중 오류 발생, 잠시 후 다시 시도해주세요.';
                        } else {
                            errorMsg += ' - 알수 없는 오류 발생 잠시 후 다시 시도해주세요.';
                        }

                        alert(errorMsg);
                    }
                }
            }
        },
        getUserInfo: async function (id) {
            console.log('유저 정보 : ' + id + ' / ');

            const result = await this.$axios({
                method: 'post',
                url: '/api/getUserInfo',
                params: {
                    id: id,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                    accesstoken: this.$store.state.token,
                },
            });

            if (result.status === 200) {
                this.id = result.data.id;
                this.user = result.data.email;
                this.password = result.data.password;
                this.name = result.data.name;
                this.nickname = result.data.nickname;
                this.mobile = result.data.mobile;
                this.profile = result.data.prifile;
                this.address = result.data.address;
                this.deleteYn = result.data.deleteYn;
            }
        },
        userManagementAuthEmail: async function (actionType) {
            if (actionType == 'DELETE') {
                if (confirm('회원정보를 정말 삭제 하시겠습니까?')) {
                    let id = this.$route.query.id;
                    let token = this.$route.query.token;

                    let result = await this.$axios({
                        method: 'post',
                        url: '/api/getUserInfoAuthEmail',
                        params: {
                            actionType: 'DELETE',
                            token: token,
                            id: id,
                        },
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                    });

                    let actionType = result.data.actionType;
                    let returnFlag = result.data.actionResCd;
                    let updateCnt = result.data.actionCnt;
                    let errorCode = result.data.errorCode;
                    let errorMsg;

                    if (result.status === 200 && returnFlag == true) {
                        errorMsg = '삭제(' + actionType + ') 성공  Communication Code = ' + result.status + ' (' + updateCnt + '건)';
                        alert(errorMsg);

                        //통신이 성공적이고 변경 건수가 0이 아닌 경우 메인으로 이동
                        this.$router.push({
                            name: 'BoardList',
                        });
                    } else {
                        errorMsg = '삭제(' + actionType + ') 실패! Communication Code = ' + result.status + '\n';

                        if (errorCode == 'ERROR01') {
                            errorMsg += errorCode + ' - 비밀번호 불일치, 비밀번호를 확인후 다시 시도해주세요. ';
                        } else if (errorCode == 'ERROR02') {
                            errorMsg += errorCode + ' - 삭제 처리중 오류 발생, 잠시 후 다시 시도해주세요.';
                        } else {
                            errorMsg += ' - 알수 없는 오류 발생 잠시 후 다시 시도해주세요.';
                        }

                        alert(errorMsg);
                    }
                }
            } else if (actionType == 'MODIFY') {
                if ((await this.formValidation()) == false) {
                    return false;
                }

                //패스워드 일치확인 검증
                if ((await this.pwValidation()) === false) {
                    this.pwValidationMsg = '비밀번호화 비밀번호 확인을 입력 해주세요.';
                    return false;
                }

                let id = this.$route.query.id;
                let token = this.$route.query.token;

                let result = await this.$axios({
                    method: 'post',
                    url: '/api/getUserInfoAuthEmail',
                    params: {
                        actionType: 'UPDATE',
                        token: token,
                        id: id,
                        email: this.user,
                        password: this.password,
                        name: this.name,
                        nickname: this.nickname,
                        mobile: this.mobile,
                        deleteYn: this.deleteYn,
                        authType: this.authType,
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                let actionType = result.data.actionType;
                let returnFlag = result.data.actionResCd;
                let updateCnt = result.data.actionCnt;
                let errorCode = result.data.errorCode;
                let errorMsg;

                if (result.status === 200 && returnFlag == true) {
                    errorMsg = '업데이트(' + actionType + ') 성공  Communication Code = ' + result.status + ' (' + updateCnt + '건)';
                    alert(errorMsg);

                    //통신이 성공적이고 변경 건수가 0이 아닌 경우 로그아웃 처리 후 메인으로 이동
                    this.$store.dispatch('logout');
                } else {
                    errorMsg = '업데이트(' + actionType + ') 실패  Communication Code = ' + result.status + '\n';

                    if (errorCode == 'ERROR01') {
                        errorMsg += errorCode + ' - 비밀번호 불일치, 비밀번호를 확인후 다시 시도해주세요. ';
                    } else if (errorCode == 'ERROR02') {
                        errorMsg += errorCode + ' - 수정 처리중 오류 발생, 잠시 후 다시 시도해주세요.';
                    } else {
                        errorMsg += ' - 알수 없는 오류 발생 잠시 후 다시 시도해주세요.';
                    }

                    alert(errorMsg);
                }
            }
        },
        getUserInfoAuthEmail: async function (id, token) {
            console.log('유저 정보 : id = ' + id + ' /  token = ' + token);

            const result = await this.$axios({
                method: 'post',
                url: '/api/getUserInfoAuthEmail',
                params: {
                    actionType: 'DETAIL',
                    id: id,
                    token: token,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                this.id = result.data.id;
                this.user = result.data.email;
                this.password = result.data.password;
                this.name = result.data.name;
                this.nickname = result.data.nickname;
                this.mobile = result.data.mobile;
                this.deleteYn = result.data.deleteYn;
                this.authType = result.data.authType;
            }
        },
        sendAuthEmail: async function (type) {
            console.log('이메일을 발송하겠습니다. TYPE : ' + type);

            var id = this.user; // 현재 입력된 아이디
            let targetUrl = type == 'changeInfo' ? '/api/noAuth/setSendAuthEmail' : '/api/noAuth/chkEmailValidity';

            //1.아이디 입력란이 공백이거나 아이디 중복 체크가 되지 않았을때.
            if (id === '' || this.idValidationFlag == false) {
                if (type == 'changeInfo') {
                    alert('이메일 인증 후 정보를 변경할 수 있도록 합니다.\n가입당시 사용하였던 올바른 아이디를 입력 후 다시 클릭해 주세요.');
                } else if (type == 'emailAuth') {
                    alert('이메일 유효성 인증을 위한 메일을 보내드리려 합니다.\n유효한 형식의 이메일을 주세요.');
                }
            } else {
                const result = await this.$axios({
                    method: 'post',
                    url: targetUrl,
                    params: {
                        email: id,
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                //가입을 위한 이메일 인증의 경우 인증코드 입력란 초기화.
                if (type == 'emailAuth') {
                    this.emailSendYN = false;
                }

                //발송결과에 따른 안내문구 표출
                if (result.status === 200) {
                    let satateCd = result.headers['state-code'];
                    if (satateCd == 'SUCCESS00') {
                        if (type == 'emailAuth') {
                            //가입을 위한 이메일 유효성 확인 메일
                            alert('이메일로 인증정보를 발송해 드렸습니다.\n받으신 인증번호를 입력해 주세요,');

                            this.emailAuthCd = result.headers['authentication-code']; //인증코드 변수에 할당
                            this.emailSendYN = true; //인증코드 입력란 활성화
                        } else if (type == 'changeInfo') {
                            //분실로인한 비밀번호를 변경하기위한 링크 발송 메일
                            alert('이메일로 인증정보를 발송해 드렸습니다.\n받으신 링크를 통해 정보를 변경해 주세요.');
                        }
                    } else if (satateCd == 'ERROR00') {
                        alert('이메일 발송중 애러가 발생하였습니다.\n잠시 후 다시 시도해 주세요.');
                    } else if (satateCd == 'ERROR01') {
                        alert('아이디로 등록되어 있지 않은 이메일 주소입니다.\n다시한번 확인해 주세요.');
                    }
                } else {
                    alert('이메일 발송중 애러가 발생하였습니다.\n잠시 후 다시 시도해 주세요.');
                }
            }
        },
        emailAuthYn: async function () {
            //입력된 인증코드와 발송시 생선된 코드의 일치여부 확인 후 emailAuthYn = true;

            let inputEmailAuthCd = document.querySelector('#inputEmailAuthCd').value;
            console.log(inputEmailAuthCd + '          ' + this.emailAuthCd);
            if (this.emailAuthCd == inputEmailAuthCd) {
                this.emailAuthYN = true;

                document.querySelector('#userId').disabled = true;

                alert('이메일 인증이 완료되었습니다.');
            } else {
                this.emailSendYN = false; //이메일 발송 FLAG
                this.emailAuthYN = false; //이메일 인증 FLAG
                this.emailAuthCd = ''; //이메일 인증 코드

                document.querySelector('#userId').disabled = false;

                alert('인증 코드가 일치하지 않거나 유효기간이 지난 코드입니다.\n코드를 다시 발급받아 주세요.');
            }
        },
        searchAddress: async function (address) {
            this.addrSearchView = true;
            this.$emit('data-to-parent', { darkYN: 'Y' });

            let searchWord = address == null ? '' : address;
            console.log(searchWord);
            const result = await this.$axios({
                method: 'post',
                url: 'https://www.juso.go.kr/addrlink/addrLinkApi.do?keyword=' + searchWord + '&confmKey=U01TX0FVVEgyMDE3MDIxNzA5MjEwODE5MDg2&resultType=json',
                params: {
                    email: '',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                if (result.data.results.juso != null) {
                    this.jsonData = result.data.results.juso;
                    this.callgetAnalyzeKeyword('Word2Vec', this.jsonData[0].roadAddr, this.jsonData[0].rn);
                }

                this.addrSearchView = true;
            }
        },
        callgetAnalyzeKeyword: async function (analyzerType, fullAdress, rn) {
            var url = '';
            if (analyzerType == 'Word2Vec') {
                url = '/api/noAuth/getAnalyzeKeyword';
            } else {
                url = '/api/noAuth/getAnalyzeKeywordJFastTest';
            }
            console.log(url + '       ----      ' + rn);
            const result = await this.$axios({
                method: 'GET',
                url: url,
                params: {
                    inputKeyword: rn,
                    analyzeType: 'model',
                    correctionYN: 'N',
                    leaningDataType: 'FULL',
                    refinementType: 'FULL',
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                //this.dataList = result.data.resuleMany; //데이터 세팅
                //this.dataListLev = result.data.resuleManyLev;

                console.log(result.data.resuleMany);
                if (result.data.code == 'SUCESS03') {
                    alert('모델을 테스트 할 수 없는 환경에서 서버가 구동되었습니다');
                }

                //this.toggleBusy(); //로딩 스피너 토글

                /*
                this.$emit('data-to-parent', [
                    ['유사 도로', result.data.resuleMany],
                    ['단어 거리계산', result.data.resuleManyLev],
                ]);
                */
                this.$emit('data-to-parent', { darkYN: 'Y', dataRight: ['유사 도로', result.data.resuleMany], dataLeft: ['단어 거리계산', result.data.resuleManyLev] });
            }
        },
        setSelectAddress: async function (selectAddress) {
            this.addrSearchView = false;
            this.address = selectAddress;

            this.$emit('data-to-parent', { initYN: 'Y' });
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
    /* color: #92badd; */
    display: inline-block;
    text-decoration: none;
    /* font-weight: 400; */
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

p.text.sm {
    margin: 0px;
}
/* STRUCTURE */

.wrapper {
    display: flex;
    align-items: center;
    flex-direction: column;
    /* justify-content: center; */
    width: 100%;
    min-height: 100%;
    /* padding: 20px; */
    /* position: relative; */
    /* z-index: 10;  */
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
    padding: 15px 0px;
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
    width: 85%;
}

input[type='button'].small,
input[type='submit'].small,
input[type='reset'].small {
    background-color: #364a5f;
    padding: 10px 15px;
    margin: 5px 20px 5px 20px;
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
    font-size: 14px;
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

input[type='text'].small {
    padding: 10px 15px;
    margin: 5px 5px 5px 20px;
    font-size: 10px;
    width: auto;
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

.addrSearch-wrap {
    display: flex;
    align-items: center;
    flex-direction: column;
    width: 100%;
    /* min-height: 100%; */
    padding: 20px;
}
.addrSearchResult-wrap {
    position: absolute;
    top: 150px;
    background-color: #fff;
    opacity: 1;
    padding: 30px 15px;
    border-radius: 5px;
    z-index: 15;
    width: 60%;
}

.addrSearchResult-wrap .searchResult-wrap {
    list-style: none;
}
.addrSearchResult-wrap .searchResultRow {
    cursor: pointer;
    font-size: 14px;
}
</style>
