import axios from 'axios';
//import Vue from 'vue';
import Vuex from 'vuex';

//Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        loginSuccess: false,
        loginError: false,
        userName: null,
    },
    mutations: {
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
        async login({ commit }, { user, password }) {
            try {
                alert('하위하위하위하위');
                const result = await axios.get('/api/login', {
                    auth: {
                        username: user,
                        password: password,
                    },
                });
                alert('하위하위하위하위');
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
