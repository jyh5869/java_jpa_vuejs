// [router > index.js 설명]
// 1. router : 라우터는 웹 페이지 간의 이동하는 방법 및 경로를 설정해주는 파일입니다
// 2. router 사용 시 App.js 파일에서는 <router-view /> 를 사용해서 로드합니다
// 3. 사용 예시 : http://localhost:8080/main

// [라우터 import 수행 실시]
import { createWebHistory, createRouter } from 'vue-router';

// [라우터 path 접속 경로 설정]
const routes = [
    {
        path: '/SideLLayout', // [경로]
        name: 'SideLLayout', // [이름]
        component: () => import('@/components/SideLLayout.vue'), // [로드 파일]
        meta: { authRequired: true },
    },
    {
        path: '/SideRLayout', // [경로]
        name: 'SideRLayout', // [이름]
        component: () => import('@/components/SideRLayout.vue'), // [로드 파일]
        meta: { authRequired: true },
    },
];

// 라우터 설정 실시
const routersSide = createRouter({
    history: createWebHistory(),
    routes,
});

// 라우터 Before Handler
/*
router.beforeEach((to, from, next) => {
    if (to.matched.some((record) => record.meta.authRequired)) {
        if (store.getters.token == null) {
            console.log('토큰 없어');
            next({
                path: '/auth',
            });
        } else {
            console.log('토큰 있어');
            next();
        }
    } else {
        next();
    }
});
*/
export default routersSide;
