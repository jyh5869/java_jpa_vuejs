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
        const promise = axios
            .post('/api/signin', JSON.stringify(params), {
                headers: { 'content-type': 'application/json' },
            })
            .then(async (res) => {
                commit('login', res);
                console.log('sotre----------------------------------');
                console.log(res);
                console.log(this.state.token);
                console.log('sotre----------------------------------');

                /*
                    await setTimeout(() => {
                        router.push('/main');
                    }, 1000);
                    */
            })
            .catch((error) => {
                console.log(error);
                alert('로그인 요청에 문제가 발생했습니다.');
            });
        promise.then(() =>
            setTimeout(() => {
                router.push('/main');
            }, 100),
        );
        //promise.then((response) => router.push('/main'));
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
