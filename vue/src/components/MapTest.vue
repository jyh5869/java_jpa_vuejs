<!-- [개별 템플릿 (뷰) 설정 실시] -->
<template>
    <hr />

    <!-- [data : 데이터 바인딩 지정] -->
    <div>
        <h1>{{ data }}</h1>
    </div>

    <!-- □ 형상 종류별 그리기 및 수정 선택 박스 □ -->
    <!-- ※ 체크박스로 그리기 및 수정여부 선택 할수 있도록 하는 마크업
        <input type="checkbox" id="checkbox" v-model="drawEnable" />
        <label for="checkbox">Draw Enable</label>
    -->
    <div>
        <button @click="drawEnabled = !drawEnabled">Draw</button>
        <span>{{ '  ' + drawEnabled + '  ' }}</span>

        <select id="type" v-model="drawType" @change="drawEnabled = true">
            <option value="Point">Point</option>
            <option value="LineString">LineString</option>
            <option value="Polygon">Polygon</option>
            <option value="Circle">Circle</option>
        </select>
    </div>

    <!-- □ 지도 생성 □ -->
    <ol-map :loadTilesWhileAnimating="true" :loadTilesWhileInteracting="true" style="height: 400px" ref="map">
        <ol-view ref="view" :center="center" :rotation="rotation" :zoom="zoom" :projection="projection" @zoomChanged="zoomChanged" @centerChanged="centerChanged" @resolutionChanged="resolutionChanged" @rotationChanged="rotationChanged" />

        <ol-tile-layer>
            <ol-source-osm />
        </ol-tile-layer>

        <!-- □ 특정 위치 OVERAY TEXT □ -->
        <ol-overlay :position="[40, 40]">
            <template v-slot="slotProps">
                <div class="overlay-content">
                    Hello world!<br />
                    Position: {{ slotProps.position }}
                </div>
            </template>
        </ol-overlay>

        <!-- □ 내 위치 찍기 MY LOCATION □ -->
        <ol-geolocation :projection="projection" @positionChanged="geoLocChange">
            <template v-slot="slotProps">
                <ol-vector-layer :zIndex="2">
                    <ol-source-vector>
                        <ol-feature ref="positionFeature">
                            <ol-geom-point :coordinates="slotProps.position"></ol-geom-point>
                            <ol-style>
                                <ol-style-icon :src="hereIcon" :scale="0.1"></ol-style-icon>
                            </ol-style>
                        </ol-feature>
                    </ol-source-vector>
                </ol-vector-layer>
            </template>
        </ol-geolocation>

        <!-- □ 랜덤좌표 클러스터링 및 에니메이션 테스트 □ -->
        <ol-interaction-clusterselect @select="featureSelectedCluster" :pointRadius="20">
            <ol-style>
                <ol-style-stroke color="red" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                <ol-style-icon :src="markerIcon" :scale="0.05"></ol-style-icon>
            </ol-style>
        </ol-interaction-clusterselect>

        <ol-animated-clusterlayer :animationDuration="500" :distance="40">
            <ol-source-vector ref="vectorsource">
                <ol-feature v-for="index in 500" :key="index">
                    <ol-geom-point :coordinates="[getRandomInRange(24, 45, 3), getRandomInRange(35, 41, 3)]"></ol-geom-point>
                </ol-feature>
            </ol-source-vector>

            <ol-style :overrideStyleFunction="overrideStyleFunction">
                <ol-style-stroke color="yellow" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.1)"></ol-style-fill>

                <ol-style-circle :radius="20">
                    <ol-style-stroke color="black" :width="15" :lineDash="[]" lineCap="butt"></ol-style-stroke>
                    <ol-style-fill color="black"></ol-style-fill>
                </ol-style-circle>

                <ol-style-text>
                    <ol-style-fill color="red"></ol-style-fill>
                </ol-style-text>
            </ol-style>
        </ol-animated-clusterlayer>

        <!-- □ 레이어 추가 및 수정 □ -->
        <ol-vector-layer :styles="vectorStyle">
            <ol-source-vector :features="zones">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend"> </ol-interaction-draw>
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>

            <!-- 만들어 지고 나서의 STYLE(태그로 넣기) 
            <ol-style>
                <ol-style-stroke color="orange" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.1)"></ol-style-fill>
                <ol-style-circle :radius="7">
                    <ol-style-fill color="orange"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
            -->
        </ol-vector-layer>
        <!-- 
            뷰3 오픈레이어스의 형상 선택 마크업
            1.ol-interaction-select 
            2.ol-interaction-clusterselect

            BUT> 둘다 SELECT타입이라 클러스터를나 형상을 클릭했을때 두개의 SELECT이벤트가 모두 동작(색상. 이벤트 꼬임 등의 문제 발생) 
            THEN> 두개가 실행이 될수 있으나 상호작용시 영향을 주지않도록 설계.
        -->
        <!-- 
            ※ 위의 이유로 해당 html 태그로 교체시 클러스터링 select event 발생시에도
            [ :condition="selectCondition ] 의 동작으로 클러스터링 에니메이션이 동작하지 않는다. 
            THEN> 중복실행시에도 문제가 되지않도록 [ :condition="selectCondition ] 제거

            <ol-interaction-select @select="featureSelected" :condition="selectCondition" :features="selectedFeatures">  
        -->
        <ol-interaction-select @select="featureSelected" :features="selectedFeatures">
            <ol-style>
                <ol-style-stroke color="red" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
            </ol-style>
        </ol-interaction-select>

        <!-- □ 지도 회전 (alt + shift 드래그) □
        <ol-interaction-dragrotate />
        -->

        <!-- □ 지도 회전 (shift 눌린채로 회전및 확대축소) □ -->
        <ol-interaction-dragrotatezoom />

        <!-- □ 형상 회잔몇 모양 변경 □ 
        <ol-interaction-transform></ol-interaction-transform>
        -->
        <!-- □ 줌 툴바 표출 □ 참고URL : https://vue3openlayers.netlify.app/componentsguide/mapcontrols/
        <ol-zoom-control v-if="zoomcontrol" />
        <ol-zoomslider-control v-if="zoomslidercontrol" />
        -->
    </ol-map>

    <!-- 
        중심점 / 줌 레벨 / 현재 해상도 / 회전 정보를 알 수 있음.  
        참고 URL : https://openlayers.org/en/latest/apidoc/module-ol_View-View.html
    -->
    <div>center : {{ currentCenter }} zoom : {{ currentZoom }} resolution : {{ currentResolution }} rotation : {{ currentRotation }}</div>
    <div class="btn-wrap navbar-nav mb-2 mb-lg-0">
        <button class="btn login btn-outline-primary" @click="setGeometry()">데이터 저장</button>
    </div>
    <hr />
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {
    name: 'HelloWorld',
    props: {
        msg: String,
    },
    data() {
        return {
            data: 'HELLO OPENLAYERS', // [데이터 정의]
            feture: null,
        };
    },
    methods: {
        testMain: function () {
            console.log('');
            console.log('[HelloWorld] : [testMain] : [start]');
            console.log('');
        },
        geoLocChange: await function (loc) {
            console.log(loc);
            console.log('내위치로 바꿔 보자');
            this.view.value.fit([loc[0], loc[1], loc[0], loc[1]], {
                maxZoom: 14,
            });
        },
        setGeometry: async function () {
            let feature = this.feture;
            console.log(this.selectedFeatures);
            console.log(feature);

            const result = await this.$axios({
                method: 'post',
                url: '/api/setGeometry',
                params: {
                    feature: feature,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                console.log(result.data);
            }
        },
    },
};

// HTML 최초 로드시 이벤트
window.onload = function () {
    console.log('');
    console.log('[HelloWorld] : [window onload] : [start]');
    console.log('');
};
</script>

<script setup>
import { ref } from 'vue';
//import { inject } from 'vue';
//import { GeoJSON } from 'ol/format';
import { Fill, Stroke, Style } from 'ol/style';
import { Collection } from 'ol';

import hereIcon from '@/assets/hereIcon.png';
import markerIcon from '@/assets/hereIcon.png';

const center = ref([40, 40]);
const projection = ref('EPSG:4326');
const zoom = ref(8);
const rotation = ref(0);
const view = ref(null);
const map = ref(null);

const currentCenter = ref(0);
const currentZoom = ref(0);
const currentResolution = ref(0);
const currentRotation = ref(0);

const modifyEnabled = ref(false);
const drawEnabled = ref(false);

/*
ZOOM 조절 툴 활성화 여부 파라메터
const zoomcontrol = ref(false);
const zoomslidercontrol = ref(true);
*/

/* 현재 지도 정보를 표출해주기 위한 함수 */
function zoomChanged(z) {
    currentZoom.value = z;
}
function resolutionChanged(r) {
    currentResolution.value = r;
}
function centerChanged(c) {
    currentCenter.value = c;
}
function rotationChanged(r) {
    currentRotation.value = r;
}

/* 내위치 찍어주기 위한 함수 */
const geoLocChange = (loc) => {
    console.log('좌표가 변경되었습니다. 해당위치(내 위치)로 이동');
    console.log(loc);
    view.value.fit([loc[0], loc[1], loc[0], loc[1]], {
        maxZoom: 3,
    });
};

/* 클러스터링 클릭시 펼쳐지는 에니메이션을 위한 함수 */
const overrideStyleFunction = (feature, style) => {
    console.log('클러스터 클릭 이벤트 발생 펼치기 진행');

    const clusteredFeatures = feature.get('features');
    const size = clusteredFeatures.length;

    const color = size > 20 ? '192,0,0' : size > 8 ? '255,128,0' : '0,128,0';
    const radius = Math.max(8, Math.min(size, 20));
    const dash = (2 * Math.PI * radius) / 6;
    const calculatedDash = [0, dash, dash, dash, dash, dash, dash];

    style.getImage().getStroke().setLineDash(dash);
    style
        .getImage()
        .getStroke()
        .setColor('rgba(' + color + ',0.5)');
    style.getImage().getStroke().setLineDash(calculatedDash);
    style
        .getImage()
        .getFill()
        .setColor('rgba(' + color + ',1)');

    style.getImage().setRadius(radius);

    style.getText().setText(size.toString());
};

/* 랜덤좌표를 생성하기 위한 함수 */
const getRandomInRange = (from, to, fixed) => {
    return (Math.random() * (to - from) + from).toFixed(fixed) * 1;
};

/* 클러스터링 선택 이벤트를 위한 함수*/
const featureSelectedCluster = (event) => {
    console.log('클러스터링이 선택되었습니다.');
    console.log(event);
};

/* 형상 그리기에를 위한 함수 (그리기시작 및 그리기종료) */
//const drawEnable = ref(false);
const drawType = ref('Polygon');
const zones = ref([]);
const selectedFeatures = ref(new Collection());

const drawstart = (event) => {
    console.log(event);
};

const drawend = (event) => {
    console.log('형상 그리기!!');
    zones.value.push(event.feature);
    selectedFeatures.value.push(event.feature);

    modifyEnabled.value = true;
    drawEnabled.value = false;
};

/* 레이어 STYLE 리턴을 위한 함수 */
function vectorStyle() {
    const style = new Style({
        stroke: new Stroke({
            color: 'orange',
            width: 3,
        }),
        fill: new Fill({
            color: 'rgba(0, 0, 255, 0.4)',
        }),
    });
    return style;
}

//const geoJsonFormat = new GeoJSON();
//const selectConditions = inject('ol-selectconditions');
//const selectCondition = selectConditions.click;

/* 형상 클릭시 이벤트발생을 위한 함수 */
function featureSelected(event) {
    console.log('형상 클릭 이벤트발생 수정 가능');

    modifyEnabled.value = false;
    if (event.selected.length > 0) {
        modifyEnabled.value = true;
    }

    selectedFeatures.value = event.target.getFeatures();
}
</script>

<!-- [개별 스타일 설정 실시] -->
<style scoped>
.colorRed {
    color: red;
}
.colorBlue {
    color: blue;
}

h3 {
    margin: 40px 0 0;
}
ul {
    list-style-type: none;
    padding: 0;
}
li {
    display: inline-block;
    margin: 0 10px;
}
a {
    color: #42b983;
}

.overlay-content {
    background: #efefef;
    box-shadow: 0 5px 10px rgb(2 2 2 / 20%);
    padding: 10px 20px;
    font-size: 16px;
}
</style>
