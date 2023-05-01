import axios from 'axios';
import router from '../index.js';

const state = {
    token: null,
    id: null,
    name: null,
    role: 'USER',
    email: null,
    nickname: null,
};

const getters = {
    token: (state) => state.token,
    id: (state) => state.id,
    email: (state) => state.email,
    nickname: (state) => state.nickname,
};

const mutations = {
    login(state, item) {
        state.token = item.headers['accesstoken'];
        state.id = item.data['id'];
        //state.role = item.data['role'];
        state.email = item.data['email'];
        state.nickname = item.data['nickname'];
    },
    logout(state) {
        state.token = null;
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
            .then((res) => {
                commit('login', res);
                console.log('sotre----------------------------------');
                console.log(res);
                console.log(this.state);
                console.log('sotre----------------------------------');

                router.push('/main');
            })
            .catch((error) => {
                console.log(error);
                alert('로그인 요청에 문제가 발생했습니다.');
            });
    },
    logout({ commit }) {
        console.log('로그아웃!!');
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
