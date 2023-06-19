<!-- [개별 템플릿 (뷰) 설정 실시] -->
<template>
    <hr />

    <!-- [data : 데이터 바인딩 지정] -->
    <div>
        <h1>{{ data }}</h1>
    </div>
    <!--
    <input type="checkbox" id="checkbox" v-model="drawEnable" />
    <label for="checkbox">Draw Enable</label>
    -->
    <button @click="drawEnabled = !drawEnabled">Draw</button>
    {{ drawEnabled }}

    <select id="type" v-model="drawType">
        <option value="Point">Point</option>
        <option value="LineString">LineString</option>
        <option value="Polygon">Polygon</option>
        <option value="Circle">Circle</option>
    </select>

    <ol-map :loadTilesWhileAnimating="true" :loadTilesWhileInteracting="true" style="height: 400px" ref="map">
        <ol-view ref="view" :center="center" :rotation="rotation" :zoom="zoom" :projection="projection" @zoomChanged="zoomChanged" @centerChanged="centerChanged" @resolutionChanged="resolutionChanged" @rotationChanged="rotationChanged" />

        <ol-tile-layer>
            <ol-source-osm />
        </ol-tile-layer>

        <!-- 특정 위치 OVERAY TEXT -->
        <ol-overlay :position="[40, 40]">
            <template v-slot="slotProps">
                <div class="overlay-content">
                    Hello world!<br />
                    Position: {{ slotProps.position }}
                </div>
            </template>
        </ol-overlay>

        <!-- 내 위치 찍기 MY LOCATION -->
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
        <!-- 



            
         -->
        <ol-interaction-clusterselect @select="featureSelected" :pointRadius="20">
            <ol-style>
                <ol-style-stroke color="green" :width="5"></ol-style-stroke>
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
                <ol-style-stroke color="red" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.1)"></ol-style-fill>

                <ol-style-circle :radius="20">
                    <ol-style-stroke color="black" :width="15" :lineDash="[]" lineCap="butt"></ol-style-stroke>
                    <ol-style-fill color="black"></ol-style-fill>
                </ol-style-circle>

                <ol-style-text>
                    <ol-style-fill color="white"></ol-style-fill>
                </ol-style-text>
            </ol-style>
        </ol-animated-clusterlayer>
        <!-- 




         -->
        <ol-vector-layer :styles="vectorStyle">
            <ol-source-vector :features="zones">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"> </ol-interaction-modify>
                <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend" />
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>
            <ol-style>
                <ol-style-stroke color="yellow" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.1)"></ol-style-fill>
                <ol-style-circle :radius="7">
                    <ol-style-fill color="yellow"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
        </ol-vector-layer>
        <ol-interaction-select @select="featureSelected" :condition="selectCondition" :features="selectedFeatures">
            <ol-style>
                <ol-style-stroke color="green" :width="10"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                <ol-style-icon :src="markerIcon" :scale="0.05"></ol-style-icon>
            </ol-style>
        </ol-interaction-select>

        <!-- 
            지도 회전 (alt + shift 드래그)

            

        
        <ol-interaction-dragrotate />
        -->
        <!-- 
            지도 회전 (shift 눌린채로 회전및 확대축소)

            

        
        <ol-interaction-dragrotatezoom />
        -->
    </ol-map>

    <div>center : {{ currentCenter }} zoom : {{ currentZoom }} resolution : {{ currentResolution }} rotation : {{ currentRotation }}</div>

    <hr />
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
export default {
    name: 'HelloWorld',

    // [부모에서 전달 받은 데이터 : 자식에서 동적 수정 불가능]
    // [형태 : <HelloWorld msg="HelloWorld"/>]
    props: {
        msg: String,
    },

    // [컴포넌트 생성 시 초기 데이터 설정 (리턴 값 지정)]
    data() {
        return {
            data: 'HELLO', // [데이터 정의]
        };
    },

    // [메소드 정의 실시]
    methods: {
        // [testMain 함수 정의 실시]
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
    },
};

// [html 최초 로드 및 이벤트 상시 대기 실시]
window.onload = function () {
    console.log('');
    console.log('[HelloWorld] : [window onload] : [start]');
    console.log('');
};
</script>

<script setup>
import { ref, inject } from 'vue';
//import { GeoJSON } from 'ol/format';
import { Fill, Stroke, Style } from 'ol/style';
import { Collection } from 'ol';

import hereIcon from '@/assets/hereIcon.png';
import markerIcon from '@/assets/hereIcon.png';
// import { ref } from 'vue';

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
/*



*/
const geoLocChange = (loc) => {
    console.log(loc);
    view.value.fit([loc[0], loc[1], loc[0], loc[1]], {
        maxZoom: 14,
    });
};
/*



*/
const overrideStyleFunction = (feature, style) => {
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

const getRandomInRange = (from, to, fixed) => {
    return (Math.random() * (to - from) + from).toFixed(fixed) * 1;
};
/*
const featureSelected = (event) => {
    console.log(event);
};





*/

//const drawEnable = ref(false);
const drawType = ref('Polygon');
const zones = ref([]);
const selectedFeatures = ref(new Collection());

const drawstart = (event) => {
    console.log(event);
};
/*
const drawend = (event) => {
    console.log(event);
};
*/
const drawend = (event) => {
    zones.value.push(event.feature);
    selectedFeatures.value.push(event.feature);

    modifyEnabled.value = true;
    drawEnabled.value = false;
};

function vectorStyle() {
    const style = new Style({
        stroke: new Stroke({
            color: 'blue',
            width: 3,
        }),
        fill: new Fill({
            color: 'rgba(0, 0, 255, 0.4)',
        }),
    });
    return style;
}

//const geoJsonFormat = new GeoJSON();
const selectConditions = inject('ol-selectconditions');
const selectCondition = selectConditions.click;

function featureSelected(event) {
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
