module.exports = {
    outputDir: '../src/main/resources/static', // 빌드 타겟 디렉토리
    devServer: {
        proxy: {
            '/api': {
                // '/api' 로 들어오면 포트 8081(스프링 서버)로 보낸다
                target: 'http://localhost:8000',
                changeOrigin: true, // cross origin 허용
                //webSocketURL: webpackArgv.nodeEnv === 'local' ? { hostname: undefined, pathname: undefined, port: '0' } : 'ws://192.168.0.8:443/ws',
            },
            '/externalApi/juso': {
                target: 'https://www.juso.go.kr',
                changeOrigin: true,
                secure: false,
                pathRewrite: { '^/externalApi/juso': '' },
                onProxyReq: (proxyReq, req, res) => {
                    console.log('외부 API 호출 : Juso');
                    const token = req.headers['authorization'] || ''; // 헤더에서 JWT 토큰을 가져옴
                    proxyReq.setHeader('Authorization', token);
                },
            },
        },
    },
};
/* 
--Vue 설정 파일-- 

※ 설정 파일 적용을 위해서는 Vue Server의 재기동이 필요.

□ proxy : 클라이언트가 자신을 통해서 다른 네트워크 서비스에 간접적으로 접속할 수 있게 해 주는 시스템

    1.요청 패턴을 정의하여 해당되는 TARGET의 네트워크와 통신할 수 있다.

    2.해당 앱과 같이  jwt토큰과 같은 인증 시스템이 적용된 경우 프록시를 통해야만 보안 및 CORS 에러 없이 타 네트워크게 접속이 가능하다.

    3.요청시 onProxyReq속성을 통해 해더에 인증 정보를 부여해서 호출 
        쿠키 포함: 요청과 함께 브라우저가 관리하는 쿠키를 포함할 수 있습니다.
        HTTP 인증 헤더 포함: 기본 인증, 다이제스트 인증과 같은 HTTP 인증 헤더를 포함할 수 있습니다.
        인증된 응답 허용: 서버가 Access-Control-Allow-Credentials 헤더를 설정하고, 자격 증명을 포함한 요청을 허용하는 경우에만 응답을 수락합니다.
*/
