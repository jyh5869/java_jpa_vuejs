import axios from 'axios';
import router from '../index.js';

const state = {
    token: null,
    refreshToken: null,
    id: null,
    name: null,
    role: 'USER',
    email: null,
    nickname: null,
    authType: null,
    loginType: null,
    createdDate: null,
    modifiedDate: null,
};

const getters = {
    token: (state) => state.token,
    refreshToken: (state) => state.refreshToken,
    id: (state) => state.id,
    email: (state) => state.email,
    name: (state) => state.name,
    authType: (state) => state.authType,
    loginType: (state) => state.loginType,
    nickname: (state) => state.nickname,
};

const mutations = {
    login(state, item) {
        state.token = item.headers['accesstoken'];
        state.refreshToken = item.headers['refreshtoken'];
        state.id = item.data['id'];
        state.role = item.data['role'];
        state.email = item.data['email'];
        state.name = item.data['name'];
        state.nickname = item.data['nickname'];
        state.authType = item.data['authType'];
        state.loginType = item.data['loginType'];
        state.createdDate = item.data['createdDate'];
        state.modifiedDate = item.data['modifiedDate'];
    },
    refresh(state, item) {
        state.token = item.headers['accesstoken'];
        state.refreshToken = item.headers['refreshtoken'];
    },
    logout(state) {
        state.token = null;
        state.refreshToken = null;
        state.id = null;
        state.role = null;
        state.email = null;
        state.nickname = null;
        state.authType = null;
        state.loginType = null;
        state.createdDate = null;
        state.modifiedDate = null;
    },
};

const actions = {
    async login({ commit }, { id, password }) {
        const params = {
            email: id,
            password: password,
        };

        await axios
            .post('/api/signin', JSON.stringify(params), {
                headers: { 'content-type': 'application/json' },
            })
            .then(async (res) => {
                if (res.data.loginRes == 'SUCCESS') {
                    commit('login', res);
                    console.log('-----------------Login Result-----------------');
                    console.log(res);
                    console.log('sotre------------Login Result-----------------');

                    router.push('/BoardList');
                } else {
                    alert('아이디 및 비밀번호를 확인해 주세요.');
                }
            })
            .catch((error) => {
                console.log(error);
                alert('로그인 요청에 알수 없는 문제가 발생했습니다.\n잠시 후 다시 시도해 주세요.');
            });
    },
    refresh({ commit }) {
        //accessToken 만료로 재발급 후 재요청시 비동기처리로는 제대로 처리가 안되서 promise로 처리함
        return new Promise((resolve, reject) => {
            const params = {
                id: this.state.id,
                email: this.state.email,
                password: this.state.password,
                nickname: this.state.nickname,
            };
            axios
                .post('/api/reissuance', JSON.stringify(params), {
                    headers: { 'content-type': 'application/json' },
                })
                .then(async (res) => {
                    res.headers['refreshtoken'] = this.state.refreshToken;
                    console.log('-----------------Token Reissuance Result-----------------');
                    console.log(res);
                    await commit('refresh', res);
                    resolve(res.headers);
                    console.log('-----------------Token Reissuance Result-----------------');
                })
                .catch((err) => {
                    console.log('refreshToken error : ', err.config);
                    reject(err.config.data);
                });
        });
    },
    async logout({ commit }) {
        console.log('로그아웃 - 로그인 페이지로 이동');
        await commit('logout');

        router.push({
            name: 'auth',
            query: { accessType: 'SIGNIN' },
        });
    },
    loginPage() {
        console.log('로그인 화면으로 이동힙니다.');
        // 로그인 화면으로 이동
        router.push({
            name: 'auth',
            query: { accessType: 'SIGNiN' },
        });
    },
};

export default {
    state,
    getters,
    mutations,
    actions,
};
