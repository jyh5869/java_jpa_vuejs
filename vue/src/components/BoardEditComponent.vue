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
        <!-- <ol-vector-layer :styles="vectorStyle()"> -->
        <ol-vector-layer>
            <ol-source-vector :features="zonesPoint">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>

            <ol-style>
                <ol-style-stroke color="orange" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                <ol-style-circle :radius="5">
                    <ol-style-stroke color="orange" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
        </ol-vector-layer>

        <ol-vector-layer>
            <ol-source-vector :features="zonesPolygon">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                <!-- <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend">
                    <ol-style>
                        <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                        <ol-style-fill color="rgba(255, 255, 0, 0.4)"></ol-style-fill>
                    </ol-style>
                </ol-interaction-draw> -->
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>

            <ol-style>
                <ol-style-stroke color="violet" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                <ol-style-circle :radius="5">
                    <ol-style-stroke color="violet" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
        </ol-vector-layer>

        <ol-vector-layer>
            <ol-source-vector :features="zonesCircle">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                <!-- <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend">
                    <ol-style>
                        <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                        <ol-style-fill color="rgba(255, 255, 0, 0.4)"></ol-style-fill>
                    </ol-style>
                </ol-interaction-draw> -->
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>

            <ol-style>
                <ol-style-stroke color="green" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                <ol-style-circle :radius="5">
                    <ol-style-stroke color="green" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
        </ol-vector-layer>

        <ol-vector-layer>
            <ol-source-vector :features="zonesLineString">
                <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                <!-- <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend">
                    <ol-style>
                        <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                        <ol-style-fill color="rgba(255, 255, 0, 0.4)"></ol-style-fill>
                    </ol-style>
                </ol-interaction-draw> -->
                <ol-interaction-snap v-if="modifyEnabled" />
            </ol-source-vector>

            <ol-style>
                <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                <ol-style-circle :radius="5">
                    <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
        </ol-vector-layer>

        <ol-interaction-select @select="featureSelected" :features="selectedFeatures">
            <ol-style>
                <ol-style-stroke color="red" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255, 200, 0, 0.2)"></ol-style-fill>
                <ol-style-circle :radius="5">
                    <ol-style-stroke color="red" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255, 200, 0, 0.2)"></ol-style-fill>
                </ol-style-circle>
            </ol-style>
        </ol-interaction-select>
        <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend">
            <ol-style>
                <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                <ol-style-fill color="rgba(255, 255, 0, 0.4)"></ol-style-fill>
            </ol-style>
        </ol-interaction-draw>
    </ol-map>
    <div class="btn-wrap navbar-nav mb-2 mb-lg-0">
        <button class="btn login btn-outline-primary" @click="setGeometry()">데이터 저장</button>
    </div>

    <hr />
    <div>{{ coordinateLineStringArr }}</div>
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
import { ref } from 'vue';
import { Fill, Stroke, Style } from 'ol/style';
import { Collection } from 'ol';
import { GeoJSON } from 'ol/format';
//import { Vector } from 'ol/source/Vector.js';
import { Feature } from 'ol';

import { Polygon } from 'ol/geom';
import { Point } from 'ol/geom';
import { Circle } from 'ol/geom';
import { LineString } from 'ol/geom';

const center = ref([40, 40]);

const projection = ref('EPSG:4326');
const zoom = ref(2.5);
const rotation = ref(0);
const view = ref(null);
const map = ref(null);

const modifyEnabled = ref(false);
const drawEnabled = ref(false);

const drawType = ref('Polygon');

const zonesLineString = ref([]);
const zonesCircle = ref([]);
const zonesPoint = ref([]);
const zonesPolygon = ref([]);

const selectedFeatures = ref(new Collection());

export default {
    name: 'HelloWorld',
    props: {
        msg: String,
    },
    data() {
        this.getGeometry();

        return {
            data: 'HELLO OPENLAYERS', // [데이터 정의]
            coordinatePolygonArr: [],
            coordinateLineStringArr: [],
            coordinatePointArr: [],
            coordinateCircleArr: [],
        };
    },
    /*
    async beforeCreate() {
        console.log('');
        console.log('[HomeComponent] : [beforeCreate] : [start]');
        console.log('설 명 : 인스턴스 초기화 준비');
        console.log('');
    },
    */
    mounted() {
        //this.getGeometry();
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
        initMap: await function () {
            //map.getLayers().forEach((layer) => layer.getSource().refresh());
            zonesLineString.value = ref([]);
            zonesCircle.value = ref([]);
            zonesPoint.value = ref([]);
            zonesPolygon.value = ref([]);
        },
        getGeometry: async function () {
            console.log('저장된 지오데이터 호출');
            const result = await this.$axios({
                method: 'get',
                url: '/api/getGeomBoard',
                params: {
                    id: '0',
                },
            });

            if (result.status === 200) {
                let coordinatePolygonArr = [];
                let coordinateLineStringArr = [];
                let coordinatePointArr = [];
                let coordinateCircleArr = [];

                result.data.forEach(function (value) {
                    let geometry = JSON.parse(value.geom_value);
                    let properties = JSON.parse(value.geom_properties);
                    let geomType = properties.type;
                    let geomValue = geometry.coordinates;
                    let geomId = value.docId;

                    if (geomType == 'Polygon') {
                        //console.log('--- Set Polygon ---');
                        let feature = new Feature({
                            type: 'Feature',
                            geometry: new Polygon(geomValue),
                            //id: geomId,
                        });

                        feature.setId(geomId);
                        feature.setProperties({ type: geomType, state: 'update' });

                        zonesPolygon.value.push(feature);
                    } else if (geomType == 'LineString') {
                        //console.log('--- Set LineString ---');
                        let feature = new Feature({
                            type: 'Feature',
                            geometry: new LineString(geomValue),
                        });

                        feature.setId(geomId);
                        feature.setProperties({ type: geomType, state: 'update' });

                        zonesLineString.value.push(feature);
                    } else if (geomType == 'Point') {
                        //console.log('--- Set Point ---');
                        let feature = new Feature({
                            type: 'Feature',
                            geometry: new Point(geomValue),
                        });

                        feature.setId(geomId);
                        feature.setProperties({ type: geomType, state: 'update' });

                        zonesPoint.value.push(feature);
                    } else if (geomType == 'Circle') {
                        console.log('--- Set Circle ---');

                        let radius = properties.radius;
                        let center = geomValue;

                        /* 이 방식으로 넣으면 feature 선택이 안되네?? */
                        let feature = new Feature({
                            type: 'Feature',
                            geometry: new Circle(center, radius),
                        });

                        feature.setId(geomId);
                        feature.setProperties({ type: geomType, state: 'update' });

                        zonesCircle.value.push(feature);
                    }
                });

                this.coordinatePolygonArr = coordinatePolygonArr;
                this.coordinateLineStringArr = coordinateLineStringArr;
                this.coordinatePointArr = coordinatePointArr;
                this.coordinateCircleArr = coordinateCircleArr;
            }
        },
        setGeometry: async function () {
            //this.initMap();
            console.log('지오데이터 전송');

            /*
            ※ 컬렉션을 피쳐로 전환하여 지오제이슨 형식으로 만들기
            참고 URLhttps://gis.stackexchange.com/questions/244656/openlayers-filter-specific-features-from-ol-collection
            */
            function filterGeometry(collection, type) {
                var selectedFeatures = [];
                //var featArray = collection.getArray();
                var featArray = collection;

                for (var i = 0; i < featArray.length; i++) {
                    let geomType = featArray[i].getGeometry().getType();
                    let id = featArray[i].getId();
                    console.log(id);
                    console.log('★★★★★★★★');
                    if (id == undefined) {
                        featArray[i].setProperties({ type: geomType, state: 'insert' });
                    } else {
                        featArray[i].setProperties({ type: geomType, state: 'update' });
                    }

                    if (geomType == type) {
                        console.log(geomType);
                        if (geomType == 'Circle') {
                            /* 중심점과 radius 가 있으면 Circle geometry 를 관리할 수 있음. */
                            let radius = featArray[i].getGeometry().getRadius();
                            let center = featArray[i].getGeometry().getCenter();

                            let feature = new Feature({
                                type: geomType,
                                geometry: new Point(center),
                                radius: radius,
                                //id: i,
                            });

                            selectedFeatures.push(feature);
                        } else {
                            //featArray[i].setProperties({ type: geomType });
                            //featArray[i].setId(i);

                            selectedFeatures.push(featArray[i]);
                        }
                    }
                }
                return selectedFeatures;
            }

            var lineStringArray = await filterGeometry(zonesLineString.value, 'LineString');
            var polygonArray = await filterGeometry(zonesPolygon.value, 'Polygon');
            var pointArray = await filterGeometry(zonesPoint.value, 'Point');
            var circleArray = await filterGeometry(zonesCircle.value, 'Circle');

            var GeoJSONFormat = new GeoJSON();

            console.log(GeoJSONFormat.writeFeatures(polygonArray));
            console.log(GeoJSONFormat.writeFeatures(lineStringArray));
            console.log(GeoJSONFormat.writeFeatures(pointArray));
            console.log(GeoJSONFormat.writeFeatures(circleArray));

            const result = await this.$axios({
                method: 'get',
                url: '/api/setGeomBoard',
                params: {
                    geomPolygons: encodeURI(GeoJSONFormat.writeFeatures(polygonArray)),
                    geomLineStrings: encodeURI(GeoJSONFormat.writeFeatures(lineStringArray)),
                    geomPoints: encodeURI(GeoJSONFormat.writeFeatures(pointArray)),
                    geomCircles: encodeURI(GeoJSONFormat.writeFeatures(circleArray)),
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
    /*
    console.log('');
    console.log('[HelloWorld] : [window onload] : [start]');
    console.log('');
    */
};
</script>

<script setup>
/* 현재 지도 정보를 표출해주기 위한 함수 */

/* 형상 그리기에를 위한 함수 (그리기시작 및 그리기종료) */
const drawstart = (event) => {
    console.log(event);
    console.log('형상 그리기 시작');
};

const drawend = (event) => {
    console.log('형상 그리기 완료!!');
    console.log(event.feature);

    const feature = event.feature;
    let geomType = feature.getGeometry().getType();

    if (geomType == 'Polygon') {
        zonesPolygon.value.push(feature);
    } else if (geomType == 'LineString') {
        zonesLineString.value.push(feature);
    } else if (geomType == 'Point') {
        zonesPoint.value.push(feature);
    } else if (geomType == 'Circle') {
        zonesCircle.value.push(feature);
    }

    selectedFeatures.value.push(feature);

    modifyEnabled.value = true;
    drawEnabled.value = false;
};

/* 레이어 STYLE 리턴을 위한 함수 */
function vectorStyle() {
    const style = [
        new Style({
            stroke: new Stroke({
                color: 'Green',
                width: 2,
            }),
            fill: new Fill({
                color: 'rgba(255,255,255,0.5)',
            }),
        }),
    ];
    return style;
}
console.log(vectorStyle());
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
