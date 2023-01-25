// [router > index.js 설명]
// 1. router : 라우터는 웹 페이지 간의 이동하는 방법 및 경로를 설정해주는 파일입니다
// 2. router 사용 시 App.js 파일에서는 <router-view /> 를 사용해서 로드합니다
// 3. 사용 예시 : http://localhost:8080/main

// [라우터 import 수행 실시]
import { createWebHistory, createRouter } from 'vue-router';
//import store from './/store';

// [라우터 path 접속 경로 설정]
const routes = [
    {
        path: '/', // [경로]
        name: 'home', // [이름]
        component: () => import('@/components/HomeComponent.vue'), // [로드 파일]
    },
    {
        path: '/hello/:document', // [경로]
        name: 'hello', // [이름]
        component: () => import('@/components/HelloComponent.vue'), // [로드 파일]
        props: true,
    },
    {
        path: '/main', // [경로]
        name: 'main', // [이름]
        component: () => import('@/components/MainComponent.vue'), // [로드 파일]]
    },
    {
        path: '/auth', // [경로]
        name: 'auth', // [이름]
        component: () => import('@/components/AuthComponent.vue'), // [로드 파일]]
    },
];

// [라우터 설정 실시]
const router = createRouter({
    history: createWebHistory(),
    routes,
});
/*
router.beforeEach((to, from, next) => {
    console.log(to.matched);
    console.log(store.getters);
    if (to.matched.some((record) => record.meta.requiresAuth)) {
        console.log('store');
        if (!store.getters.isLoggedIn) {
            next({
                name: 'auth',
            });
        } else {
            next();
        }
    } else {
        next();
    }
});
*/
export default router;
