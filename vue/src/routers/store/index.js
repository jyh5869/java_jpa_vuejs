import { createStore } from 'vuex';
import createPersistedState from 'vuex-persistedstate';
import modules from './module.js';

//해당 값들을 로컬 스토리지에 저장하여 필요시 사용하기 위함
const persistedState = createPersistedState({
    paths: ['token', 'refreshToken', 'id', 'name', 'role', 'email', 'nickname', 'authType'],
});

export default createStore({
    state: modules.state,
    getters: modules.getters,
    mutations: modules.mutations,
    actions: modules.actions,
    plugins: [persistedState],
});
