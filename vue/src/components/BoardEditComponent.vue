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

        <!-- □ 레이어 추가 및 수정 □ -->
        <ol-vector-layer :styles="vectorStyle">
            <ol-source-vector :features="zones">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend"> </ol-interaction-draw>
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>
        </ol-vector-layer>

        <ol-interaction-select @select="featureSelected" :features="selectedFeatures">
            <ol-style>
                <ol-style-stroke color="red" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
            </ol-style>
        </ol-interaction-select>
    </ol-map>
    <div class="btn-wrap navbar-nav mb-2 mb-lg-0">
        <button class="btn login btn-outline-primary" @click="setGeometry()">데이터 저장</button>
    </div>
    <hr />
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
import { ref } from 'vue';
import { Fill, Stroke, Style } from 'ol/style';
import { Collection } from 'ol';

const center = ref([40, 40]);
const projection = ref('EPSG:4326');
const zoom = ref(8);
const rotation = ref(0);
const view = ref(null);
const map = ref(null);

const modifyEnabled = ref(false);
const drawEnabled = ref(false);

const drawType = ref('Polygon');
const zones = ref([]);
const selectedFeatures = ref(new Collection());

export default {
    name: 'HelloWorld',
    props: {
        msg: String,
    },
    data() {
        return {
            data: 'HELLO OPENLAYERS', // [데이터 정의]
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
            console.log(selectedFeatures);
            console.log(selectedFeatures.value.getKeys());
            console.log(selectedFeatures.value.get('length'));
            console.log(selectedFeatures.value.get('length'));
            console.log(selectedFeatures.value.get);
            /*
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
            */
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
/* 현재 지도 정보를 표출해주기 위한 함수 */

/* 형상 그리기에를 위한 함수 (그리기시작 및 그리기종료) */
//const drawEnable = ref(false);

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
