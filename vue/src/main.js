import { createApp } from 'vue'
//import Vue           from 'vue'
import App1          from './App.vue'
import routers       from './routers/index.js'

import axios from 'axios'
import BootstrapVue3 from 'bootstrap-vue-3'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css'

/*  ※ 기본 세팅
    import App2 from './App copy.vue'
    createApp(App2).mount('#main')
*/

/*
    ※ 참고링크1 : https://kkh0977.tistory.com/1951
    ※ 참고링크2 : https://kkh0977.tistory.com/1952
    1. 앱 생성 실시
    2. 라우터 사용 설정
    3. main 아이디 : 렌더링 시작점 
*/


// axios instance 생성
// const instance = axios.create({
//     //baseURL: process.env.VUE_APP_API_URL
//     baseURL : "http://localhost:8000"
// });
console.log(process.env.VUE_APP_API_URL);

// 요청 인터셉터 추가
// Promise Chaining이란 무었인가???????????????????? 
axios.interceptors.request.use(
    function (config) {
      // 요청이 전달되기 전에 작업 수행
      console.log("요청했다.");
      return config;
    },
    function (error) {
      // 요청 오류가 있는 경우 작업 수행
      console.log("에러가 발생했다!!!!!!!!!!!!!!!!!!!!!!!." + error);
      return Promise.reject(error);
    },
  );

  axios.interceptors.response.use(
    function (response) {
      return response;
    },
    function (error) {
        console.log("-----------");
        console.log(error);
        console.log("-----------");
      if (error.response && error.response.status) {
        switch (error.response.status) {
          // status code가 401인 경우 `logout`을 커밋하고 `/login` 페이지로 리다이렉트
        case 401:
            //store.commit('auth/logout');
            //router.push('/login').catch(() => {});
            // 이행되지 않는 Promise를 반환하여 Promise Chaining 끊어주기
            console.log("에러가 발생했다. 401" + error);
            return new Promise(() => {});
        case 200:
            console.log("에러가 발생했다. 200" + error);
            return new Promise(() => {});
        case 500:
            console.log("에러가 발생했다. 500" + error);
            return new Promise(() => {});
        default:
            console.log("에러가 발생했다.");
            return Promise.reject(error);
        }
      }
      console.log("-----------");
        console.log(error);
        console.log("-----------");
      //return Promise.reject(error);
    },
  );



const app = createApp(App1);
app.config.globalProperties.$axios = axios; 

app.use(routers);
app.use(BootstrapVue3)
app.mount('#main');





