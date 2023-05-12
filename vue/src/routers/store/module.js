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
};

const getters = {
    token: (state) => state.token,
    refreshToken: (state) => state.refreshToken,
    id: (state) => state.id,
    email: (state) => state.email,
    nickname: (state) => state.nickname,
};

const mutations = {
    login(state, item) {
        state.token = item.headers['accesstoken'];
        state.refreshToken = item.headers['refreshtoken'];
        state.id = item.data['id'];
        state.role = item.data['role'];
        state.email = item.data['email'];
        state.nickname = item.data['nickname'];
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
    },
};

const actions = {
    login({ commit }, { id, password }) {
        const params = {
            email: id,
            password: password,
        };

        axios
            .post('/api/signin', JSON.stringify(params), {
                headers: { 'content-type': 'application/json' },
            })
            .then(async (res) => {
                commit('login', res);
                console.log('-----------------Login Result-----------------');
                console.log(res);
                console.log('sotre------------Login Result-----------------');

                router.push('/main');
            })
            .catch((error) => {
                console.log(error);
                alert('로그인 요청에 문제가 발생했습니다.');
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
    logout({ commit }) {
        console.log('로그아웃 - 로그인 페이지로 이동');
        commit('logout');

        router.push({
            name: 'auth',
            query: { accessType: 'SIGNIN' },
        });
    },
    loginPage() {
        alert('로그인 화면으로 이동한다?');
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
