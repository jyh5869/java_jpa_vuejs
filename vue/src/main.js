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




const app = createApp(App1);
app.config.globalProperties.$axios = axios; 
app.use(routers);
app.use(BootstrapVue3)
app.mount('#main');





