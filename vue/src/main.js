import { createApp } from 'vue';
import App from './App.vue';
import routers from './routers/index.js';

import axios from 'axios';
import BootstrapVue3 from 'bootstrap-vue-3';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css';

import store from './routers/store/index.js';

const app = createApp(App);
console.log(store.state.email);
console.log(store.state.token == null ? 'AccessToken Empty' : 'AccessToken : ' + store.state.token.substr(0, 20) + '...');
console.log(store.state.refreshToken == null ? 'RefreshToken Empty' : 'RefreshToken : ' + store.state.refreshToken.substr(0, 20) + '...');
//axios instance 생성
const instance = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
    headers: {
        accesstoken: store.state.token,
        refreshToken: store.state.refreshToken,
    },
});
// 요청 인터셉터 추가
// Promise Chaining이란 무었인가????????????????????
instance.defaults.timeout = 10000;
instance.interceptors.request.use(
    function (config) {
        // 요청이 전달되기 전에 작업
        return config;
    },
    function (error) {
        // 요청 오류가 있는 경우 작업
        console.log('ERROR - Request 오류발생 : ' + error);
        return Promise.reject(error);
    },
);

instance.interceptors.response.use(
    function (response) {
        return response;
    },
    async function (error) {
        if (error.response && error.response.status) {
            const errorAPI = error.response.config;
            switch (error.response.status) {
                case 401:
                    console.log('ERROR - Response 401 오류발생 : ' + error);
                    if (errorAPI.retry == undefined && store.state.refreshToken != null) {
                        //리프래쉬 토큰이 있을경우 토큰 재발급 후 Axios 재요청

                        await store.dispatch('refresh'); //토큰 재발급 Action

                        errorAPI.retry = true; //재요청 변수 추가
                        errorAPI.headers['accesstoken'] = store.state.token;
                        errorAPI.headers['refreshtoken'] = store.state.refreshToken;

                        return await axios(errorAPI); //다시 axios 요청
                    } else {
                        //리프레쉬 토큰 기간마감일 경우 로그아웃 후 로그인 페이지로 이동
                        store.commit('logout');
                    }
                    return new Promise(() => {});
                case 403:
                    console.log('ERROR - Response 403 오류발생 : ' + error);
                    return new Promise(() => {});
                case 200:
                    console.log('ERROR - Response 200 오류발생 : ' + error);
                    return new Promise(() => {});
                case 500:
                    console.log('ERROR - Response 500 오류발생 : ' + error);
                    return new Promise(() => {});
                default:
                    //이행되지 않는 Promise를 반환하여 Promise Chaining 끊어주기
                    console.log('에러가 발생했다.');
                    return Promise.reject(error);
            }
        }
        return Promise.reject(error);
    },
);

app.config.globalProperties.$axios = instance;
app.config.globalProperties.$store = store;

app.use(store);
app.use(routers);
app.use(BootstrapVue3);
app.mount('#main');
