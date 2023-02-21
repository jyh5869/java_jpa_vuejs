import { createApp } from 'vue';
import App from './App.vue';
import routers from './routers/index.js';

import axios from 'axios';
import BootstrapVue3 from 'bootstrap-vue-3';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css';

import store from './routers/store/index.js';
// console.log('★★★★★');
// console.log(store);
// console.log('★★★★★');
const app = createApp(App);

//axios instance 생성
const instance = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
    headers: { accesstoken: store.state.token },
});

// 요청 인터셉터 추가
// Promise Chaining이란 무었인가????????????????????
instance.defaults.timeout = 5000;
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
    function (error) {
        if (error.response && error.response.status) {
            switch (error.response.status) {
                // status code가 401인 경우 `logout`을 커밋하고 `/login` 페이지로 리다이렉트
                case 401:
                    // 이행되지 않는 Promise를 반환하여 Promise Chaining 끊어주기
                    store.commit('logout');
                    console.log('ERROR - Response 401 오류발생 : ' + error);
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
