<!--
[App.vue 설명]
1. App.vue 는 Vue 애플리케이션의 최상위 컴포넌트입니다

2. template : 
   - 화면 상에 표시할 요소 작성 실시
   - 컴포넌트의 모든 마크업 구조와 디스플레이 로직 작성

3. script : 
   - import 구문을 사용해 template에서 사용할 컴포넌트 불러온다
   - export default 구문에서 해당 App 컴포넌트가 main.js 파일에서 불러와지도록 내용 작성 실시
   - export default 구문에서 모듈의 함수, 객체, 변수 등을 다른 모듈에서 가져다 사용 할 수 있도록 내보냅니다

4. style : 
   - 스타일 지정 실시

5. props : 부모가 전달한 데이터를 받을 때 사용합니다 (부모 쪽에서 자식 객체 생성 필요) : 자식쪽에서 동적 변경 불가능

6. {{ message }} : 데이터 바인딩 시 사용

7. @click : 클릭 이벤트 적용 약어 (원본 : v-on:click)

8. data : 컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)

9. methods : 메소드 정의 실시

10. this.$parent.부모 메소드 명칭 : 부모 메소드 호출 실시

11. v-model : 데이터 양방향 바인드를 실시합니다 (ex: input text 수정 시 <h1> 태그 값도 동시 변경)

12. v-html : 원시 HTML 형식 문자열 데이터를 실제 HTML 로 변경해줍니다

13. v-bind : disabled : 버튼 사용 가능 여부 (클릭) 처리 설정

14. v-for : 배열 데이터를 for 반복문을 돌면서 순차적으로 확인합니다

15. v-bind:class : 특정 조건을 만족 (true) 할 시 삼항식을 사용해 클래스 지정 분기 처리 실시

16. v-bind:style : css 스타일 코드를 지정할 수 있습니다

17. v-if : 조건 값에 따라서 렌더링 (표시) 를 수행합니다

18. v-if v-else : 조건 값이 true 일 경우 if 분기 처리 false 일 경우 else 분기 처리를 수행합니다

19. v-show : v-if 문과 유사 하지만, 초기 렌더링 시 true, false 상관 없이 무조건 dom에 렌더링 됩니다

20. @click.prevent : 원래 동작하는 클릭 이벤트 막고 , 지정한 메소드로 이벤트 핸들링 수행

21. @keydown : 키 입력 이벤트 값을 확인 할 수 있습니다 (한글 경우는 Process 가 출력됨)

22. @input : 특정 변수에 입력한 값을 대입 할 수 있습니다

23. watch : data 및 computed 속성의 값이 변경 된 상태 감지 실시

24. style scoped : scoped 를 써주어야 해당 컴포넌트의 요소들에만 스타일이 적용됩니다 . (아니면 프로젝트 전체 요소에 스타일 적용됨)

25. router-view : 라우터에서 설정한 뷰 로드 실시
-->

<!-- [애플리케이션 공통 템플릿 (뷰) 지정] -->
<template>
    <!-- [팝업창 동작시 어두운 배경] -->
    <div v-if="darKYN == 'Y'" class="blackBackground"></div>

    <!-- [토스트 창과 작업 히스토리 관리 컴포넌트] -->
    <b-row>
        <b-col cols="4">
            <ToastLayout :historyUseYnProp="historyUseYn" :showHistoryProp="showHistory" :showToastProp="showToast" :toastDataProp="toastData" />
        </b-col>
        <b-col cols="6"></b-col>
    </b-row>

    <!-- [메인 로고 및 텍스트] -->
    <b-row id="dataContainer">
        <h1><img src="./assets/logo.png" id="icon" alt="User Icon" />{{ data }}</h1>
    </b-row>

    <!-- [헤더 컴포넌트] -->
    <HeaderLayout @user-logged-out="handleUserLogout" />

    <!-- [데이터 컨테이너] -->
    <b-row>
        <b-col cols="2">
            <LoadingSpinner v-if="showSpinner == true" :spinnerDataProp="spinnerData" :showSpinnerProp="showSpinner" />
            <SideLLayout v-if="dataRight.length != 0" :parsingList="dataRight" />
        </b-col>
        <b-col cols="8">
            <router-view :key="$route.fullPath" @data-to-parent="receiveDataFromChild" @toast-to-parent="receiveDataFromChildToast" />
        </b-col>
        <b-col cols="2">
            <LoadingSpinner v-if="showSpinner == true" :spinnerDataProp="spinnerData" :showSpinnerProp="showSpinner" />
            <SideRLayout v-if="dataLeft.length != 0" :parsingList="dataLeft" />
        </b-col>
    </b-row>

    <!-- [푸터 컴포넌트] -->
    <FooterLayout />
</template>

<!-- [애플리케이션 공통 스크립트 지정] -->
<script>
import HeaderLayout from './commonLayout/HeaderLayout.vue';
import FooterLayout from './commonLayout/FooterLayout.vue';
import SideLLayout from './commonLayout/SideLLayout.vue';
import SideRLayout from './commonLayout/SideRLayout.vue';
import ToastLayout from './commonAction/ToastLayout.vue';
import LoadingSpinner from './commonAction/LoadingSpinner.vue';

// [export 설정 실시]
export default {
    name: 'App',
    // [동적 변환 컴포넌트 페이지 : 기본 표시 부분]
    components: {
        HeaderLayout, //[HeaderLayout 컴포넌트]
        FooterLayout, //[FooterLayout 컴포넌트]
        SideLLayout, //[SideLLayout 컴포넌트]
        SideRLayout, //[SideRLayout 컴포넌트]
        ToastLayout, //[ToastLayout 컴포넌트]
        LoadingSpinner, //[LoadingSpinner 컴포넌트]
    },
    //[컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)]
    data() {
        return {
            data: 'APP VUE', //[데이터 지정]
            dataRight: [],
            dataLeft: [],
            darKYN: 'N',
            historyUseYn: this.$store.state.token != null ? true : false,
            showToast: false, //[토스트 표출 여부]
            showHistory: false,
            toastData: {}, //[토스트창 정보]
            showSpinner: false,
            spinnerData: {},
            isLoading: false,
        };
    },
    methods: {
        toggleLoading() {
            this.isLoading = !this.isLoading;
        },
        //자식 컴포넌트에서 전달된 데이터에 따른 동적 실행(라우터)
        receiveDataFromChild(data) {
            console.log('Received data from ChildComponent:', data);

            //1. 사이드 레이어웃 컴포넌트 컨트롤
            if (data.darkYN != undefined) {
                this.darKYN = data.darkYN;
            }
            if (data.dataRight != undefined) {
                this.dataRight = data.dataRight;
            }
            if (data.dataLeft != undefined) {
                this.dataLeft = data.dataLeft;
            }
            if (data.initYN == 'Y') {
                this.dataRight = [];
                this.dataLeft = [];
                this.darKYN = null;
            }

            //3.로딩 스피너 컴포넌트 컨트롤
            if (data.showSpinner != undefined) {
                this.showSpinner = data.showSpinner;
            }
            if (data.spinnerData != undefined) {
                this.spinnerData = data.spinnerData;
            }
        },

        //1. 히스토리 서비스 컴포넌트 컨트롤
        receiveDataFromChildToast(data) {
            console.log('User logged out!', data);

            //히스토리 서비스 활성화 여부 1. true : 히스토리 열기 상태 / 2. false : 히스토리 닫기 상태
            if (data.historyUseYn != undefined) {
                this.historyUseYn = data.historyUseYn;
            }

            //히스토리 리스트 표출 여부
            if (data.showHistory != undefined) {
                this.showHistory = data.showHistory;
            }

            //히스토리 서비스의 토스트 표출을 위한 데이터
            if (data.toast != undefined) {
                this.toastData = {
                    delay: 3000,
                    autoHide: true,
                    noFade: false,
                    noCloseButton: false,
                    variant: data.toast.variant,
                    bodyClass: '',
                    headerClass: '',
                    title: data.toast.title,
                    body: data.toast.body,
                };
            }

            //Toast 상태를 변경하기 전에 일단 false로 초기화
            this.showToast = false;
            //강제로 reactivity 트리거하기 위해 다음 tick에서 showToast를 true로 변경
            this.$nextTick(() => {
                if (data.showToast !== undefined) {
                    // 토스트 표시 여부 설정
                    this.showToast = data.showToast;
                }
            });
        },
        //자식 컴포넌트에서 전달된 데이터에 따른 동적 실행(헤더 로그아웃)
        handleUserLogout(data) {
            console.log('User logged out!', data);

            //히스토리 버튼  표출여부(false)
            this.historyUseYn = data.historyUseYn;

            //히스토리 표출 여부(false)
            this.showHistory = data.showHistory;

            //토스트 표출 여부(true)
            this.showToast = data.showToast;

            //표출될 로그아웃 토스트 내용
            if (data.toast != undefined) {
                this.toastData = {
                    delay: 3000,
                    autoHide: true,
                    noFade: false,
                    noCloseButton: false,
                    variant: data.toast.variant,
                    bodyClass: '',
                    headerClass: '',
                    title: data.toast.title,
                    body: data.toast.body,
                };
            }
        },
    },
};
</script>

<!-- [애플리케이션 공통 스타일 지정] -->
<style>
#dataContainer {
    color: #42b883;
    position: relative;
    z-index: 9;
}

#dataContainer h1 {
    display: inline-block;
}

#dataContainer img {
    width: 50px;
}

#main {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
}

.blackBackground {
    background-color: black;
    position: fixed;
    z-index: 10;
    width: 100%;
    height: 100%;
    opacity: 0.7;
}

.d-flex {
    display: flex;
}

.mt-3 {
    margin-top: 1rem;
}
</style>
