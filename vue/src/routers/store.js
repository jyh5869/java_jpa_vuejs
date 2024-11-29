import axios from 'axios';
import { createStore } from 'vuex';

export default createStore({
    state: {
        //전역적으로 사용할 변수명 세팅
        loginSuccess: false,
        loginError: false,
        userName: null,
    },
    mutations: {
        //state의 변수를 변경
        loginSuccess(state, { user, password }) {
            state.loginSuccess = true;
            state.userName = user;
            state.password = password;
        },
        loginError(state, { user, password }) {
            state.loginError = true;
            state.userName = user;
            state.userName = password;
        },
    },
    actions: {
        //실행하여 mutation을 실행 참조링크 : https://ux.stories.pe.kr/149
        async login({ commit }, { user, password }) {
            try {
                const result = await axios.get('/api/login', {
                    auth: {
                        username: user,
                        password: password,
                    },
                });

                if (result.status === 200) {
                    commit('loginSuccess', {
                        userName: user,
                        userPass: password,
                    });
                }
            } catch (err) {
                commit('loginError', {
                    userName: user,
                });
                throw new Error(err);
            }
        },
    },
    getters: {
        isLoggedIn: (state) => state.loginSuccess,
        hasLoginErrored: (state) => state.loginError,
        getUserName: (state) => state.userName,
        getUserPass: (state) => state.userPass,
    },
    modules: {},
});
