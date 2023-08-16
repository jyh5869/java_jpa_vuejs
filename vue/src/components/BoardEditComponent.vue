<!-- [개별 템플릿 (뷰) 설정 실시] -->
<template>
    <hr />

    <!-- [data : 데이터 바인딩 지정] -->
    <div>
        <h1>{{ data }}</h1>
    </div>
    <div class="row g-3">
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
                <ol-source-cluster :distance="1">
                    <ol-source-vector :features="zonesPoint">
                        <ol-interaction-modify v-if="modifyEnabled" :features="selectedFeatures"></ol-interaction-modify>
                        <ol-interaction-snap v-if="modifyEnabled" />

                        <ol-overlay :ref="'lineString_ovl_' + item.getId()" :class="'overlay-wrap point ' + item.getId()" :id="'point_ovl_' + item.getId()" v-for="(item, index) in zonesPoint" :key="index" :position="[item.getGeometry().getExtent()[0] + 3, item.getGeometry().getExtent()[1] + 5]">
                            <template v-slot="position">
                                <div class="overlay-content" @click="geomEvent(item, position)"></div>
                            </template>
                        </ol-overlay>
                    </ol-source-vector>
                </ol-source-cluster>

                <ol-style :overrideStyleFunction="overrideStyleFunction">
                    <ol-style-stroke color="orange" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255,255,255,0.5)"></ol-style-fill>
                    <ol-style-circle :radius="10">
                        <ol-style-stroke color="#fff" :width="2"></ol-style-stroke>
                        <ol-style-fill color="#3399CC"></ol-style-fill>
                    </ol-style-circle>
                    <ol-style-text>
                        <ol-style-fill color="#fff" :width="1"></ol-style-fill>
                    </ol-style-text>
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
                    <ol-overlay :ref="'lineString_ovl_' + item.getId()" class="overlay-wrap" :id="'polygon_ovl_' + item.getId()" v-for="(item, index) in zonesPolygon" :key="index" :position="[item.getGeometry().getExtent()[2], item.getGeometry().getExtent()[3]]">
                        <template v-slot="position">
                            <div class="overlay-content" @click="geomEvent(item, position)"></div>
                        </template>
                    </ol-overlay>
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

                    <ol-overlay :ref="'lineString_ovl_' + item.getId()" class="overlay-wrap" :id="'circle_ovl_' + item.getId()" v-for="(item, index) in zonesCircle" :key="index" :position="[item.getGeometry().getExtent()[2], item.getGeometry().getExtent()[3]]">
                        <template v-slot="position">
                            <div class="overlay-content" @click="geomEvent(item, position)"></div>
                        </template>
                    </ol-overlay>
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
                    <ol-interaction-snap v-if="modifyEnabled" />

                    <ol-overlay :ref="'lineString_ovl_' + item.getId()" :class="'overlay-wrap' + item.getId()" :id="'lineString_ovl_' + item.getId()" v-for="(item, index) in zonesLineString" :key="index" :position="[item.getGeometry().getExtent()[2], item.getGeometry().getExtent()[3]]">
                        <template v-slot="position">
                            <div class="overlay-content" @click="geomEvent(item, position)"></div>
                        </template>
                    </ol-overlay>
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
                    <ol-style-circle :radius="10">
                        <ol-style-stroke color="red" :width="1"></ol-style-stroke>
                        <ol-style-fill color="#3399CC"></ol-style-fill>
                    </ol-style-circle>
                    <ol-style-text>
                        <ol-style-fill color="#fff" :width="1"></ol-style-fill>
                    </ol-style-text>
                </ol-style>
            </ol-interaction-select>
            <ol-interaction-draw v-if="drawEnabled" :stopClick="true" :type="drawType" @drawstart="drawstart" @drawend="drawend">
                <ol-style>
                    <ol-style-stroke color="blue" :width="2"></ol-style-stroke>
                    <ol-style-fill color="rgba(255, 255, 0, 0.4)"></ol-style-fill>
                </ol-style>
            </ol-interaction-draw>
        </ol-map>

        <div class="col-md-6">
            <label for="userEmail" class="form-label">User Email</label>
            <input type="email" class="form-control" v-model="userEmail" readonly />
        </div>
        <div class="col-md-6">
            <label for="userNm" class="form-label">User Name</label>
            <input type="text" class="form-control" v-model="userNm" readonly />
        </div>
        <div class="col-12">
            <label for="title" class="form-label">title</label>
            <input type="text" class="form-control" placeholder="1234 Main St" v-model="title" />
        </div>
        <div class="col-12">
            <label for="contents1" class="form-label">Contents1</label>
            <input type="text" class="form-control" placeholder="1234 Main St" v-model="contents1" />
        </div>
        <div class="col-12">
            <label for="contents2" class="form-label">Contents2</label>
            <input type="text" class="form-control" placeholder="Apartment, studio, or floor" v-model="contents2" />
        </div>
        <div class="col-md-6">
            <label for="userAdress" class="form-label">User Adress</label>
            <input type="text" class="form-control" v-model="userAdress" />
        </div>
        <div class="col-md-4">
            <label for="state" class="form-label">State</label>
            <select id="state" class="form-select" v-model="state">
                <option selected>Choose...</option>
                <option>...</option>
            </select>
        </div>
        <div class="col-md-2">
            <label for="zipCd" class="form-label">Zip</label>
            <input type="text" class="form-control" v-model="zipCd" />
        </div>
        <div class="col-12">
            <div class="form-check">
                <input class="form-check-input" id="useYn" type="checkbox" v-model="useYn" />
                <label class="form-check-label" for="useYn"> Check User Y/N </label>
            </div>
        </div>

        <div class="btn-wrap navbar-nav mb-2 mb-lg-0">
            <button class="btn login btn-outline-primary" @click="setGeometry()">데이터 저장</button>
        </div>

        <div class="col-12">
            <button type="button" class="btn mx-1 px-3 float-right btn-outline-success" v-if="actionType == 'insert' || actionType == 'update'" @click="getBoardList">리스트</button>
            <button type="button" class="btn mx-1 px-3 float-right btn-outline-danger" v-if="actionType == 'update'" @click="setBoardDelete">삭제</button>
        </div>
    </div>
    <hr />
</template>

<!-- [개별 스크립트 설정 실시] -->
<script>
import { ref } from 'vue';
import { Fill, Stroke, Style } from 'ol/style';
import { Collection } from 'ol';
import { GeoJSON } from 'ol/format';
//import { Vector } from 'ol/source/Vector.js';
import { Feature } from 'ol';
//import { Select } from 'ol/interaction';

import { Polygon } from 'ol/geom';
import { Point } from 'ol/geom';
import { Circle } from 'ol/geom';
import { LineString } from 'ol/geom';

const center = ref([40, 40]);

const projection = ref('EPSG:4326');
const zoom = ref(2.5);
const currentZoom = ref();
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
const zonesDelete = ref([]);

const selectedFeatures = ref(new Collection());

export default {
    name: 'HelloWorld',
    props: {
        msg: String,
    },
    data() {
        let actionType;
        let document = {};

        let callType = JSON.parse(this.$route.params.document).callType;

        //지도 초기화
        this.initMap();

        if (callType == 'Detail') {
            document = JSON.parse(this.$route.params.document).boardData;
            actionType = 'update';

            this.getGeometry(document.board_sq);
        } else if (callType == 'Write') {
            actionType = 'insert';
        }

        return {
            data: 'HELLO OPENLAYERS', // [데이터 정의]
            actionType: actionType, //동작타입: 1.insert, 2.update
            docId: document.docId, //게시글 ID(문자열)
            boardSq: document.board_sq, //게시글 INDEX(정수)
            callType: callType, //진입경로
            coordinatePolygonArr: [], //Polygon 배열
            coordinateLineStringArr: [], //LineString 배열
            coordinatePointArr: [], //Point 배열
            coordinateCircleArr: [], //Circle 배열
            userEmail: callType == 'Detail' ? document.user_email : this.$store.getters.email, //이메일: 상세보기의 경우 게시글정보, 글쓰기의경우 이용자정보
            userNm: callType == 'Detail' ? document.user_name : this.$store.getters.name, //이름: 상세보기의 경우 게시글정보, 글쓰기의경우 이용자정보
            userAdress: document.user_adress, // 사용자 주소
            title: document.title, //게시글 제목
            contents1: document.contents1, //게시글 내용1
            contents2: document.contents2, ///게시글 내용2
            state: document.state, //게시글 상태
            zipCd: document.zip_cd, //게시글 우편번호
            useYn: document.use_yn, //게시글 사용여부
        };
    },
    mounted() {
        //DOM 렌더링 완료 후 실행
    },
    updated() {
        //DOM 상태 및 데이터 변경 완료 후 실행
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
        zoomChanged: await function (zoomLevel) {
            //console.log('줌 레벨 변경 : ' + zoomLevel);
            currentZoom.value = zoomLevel;
        },
        initMap: await function () {
            zonesLineString.value.length = 0;
            zonesCircle.value.length = 0;
            zonesPoint.value.length = 0;
            zonesPolygon.value.length = 0;
        },
        getGeometry: async function (boardSq) {
            console.log('저장된 지오데이터 호출 BoardSq : ' + boardSq);

            const result = await this.$axios({
                method: 'get',
                url: '/api/getGeomBoard',
                params: {
                    boardSq: boardSq,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                    accesstoken: this.$store.state.token,
                },
            });

            if (result.status === 200) {
                let coordinatePolygonArr = [];
                let coordinateLineStringArr = [];
                let coordinatePointArr = [];
                let coordinateCircleArr = [];

                await result.data.forEach(function async(value) {
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
                        feature.setProperties({ type: geomType, state: 'update', id: geomId });

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

                this.featureOverlayInit();
            }
        },
        setGeometry: async function () {
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
                            feature.setId(featArray[i].getId());
                            if (id == undefined) {
                                feature.setProperties({ type: geomType, state: 'insert' });
                            } else {
                                feature.setProperties({ type: geomType, state: 'update' });
                            }

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

            console.log('checkYn------------------------------------>' + this.useYn);
            const result = await this.$axios({
                method: 'get',
                url: '/api/setGeomBoard',
                params: {
                    actionType: this.actionType,
                    docId: this.docId,
                    boardSq: this.boardSq,
                    geomPolygons: encodeURI(GeoJSONFormat.writeFeatures(polygonArray)),
                    geomLineStrings: encodeURI(GeoJSONFormat.writeFeatures(lineStringArray)),
                    geomPoints: encodeURI(GeoJSONFormat.writeFeatures(pointArray)),
                    geomCircles: encodeURI(GeoJSONFormat.writeFeatures(circleArray)),
                    geomDeleteArr: encodeURI(zonesDelete.value),
                    userEmail: this.userEmail,
                    userNm: this.userNm,
                    userAdress: this.userAdress,
                    title: this.title,
                    contents1: this.contents1,
                    contents2: this.contents2,
                    state: this.state,
                    zipCd: this.zipCd,
                    useYn: this.useYn,
                },
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (result.status === 200) {
                console.log(result.data);
                this.getBoardList();
            }
        },
        geomEvent: async function (targetFeature, position) {
            let targetFeatureId = targetFeature.getId();
            let targetGemonType = targetFeature.getGeometry().getType();
            console.log(position);
            if (targetGemonType == 'LineString') {
                zonesLineString.value.forEach(function (feature, index) {
                    let arrFeatureId = feature.getId();

                    if (targetFeatureId == arrFeatureId) {
                        zonesLineString.value.splice(index, 1);
                    }
                });
            } else if (targetGemonType == 'Polygon') {
                zonesPolygon.value.forEach(function (feature, index) {
                    let arrFeatureId = feature.getId();

                    if (targetFeatureId == arrFeatureId) {
                        zonesPolygon.value.splice(index, 1);
                    }
                });
            } else if (targetGemonType == 'Point') {
                zonesPoint.value.forEach(function (feature, index) {
                    let arrFeatureId = feature.getId();

                    if (targetFeatureId == arrFeatureId) {
                        zonesPoint.value.splice(index, 1);
                    }
                });
            } else if (targetGemonType == 'Circle') {
                zonesCircle.value.forEach(function (feature, index) {
                    let arrFeatureId = feature.getId();

                    if (targetFeatureId == arrFeatureId) {
                        zonesCircle.value.splice(index, 1);
                    }
                });
            }

            //삭제 배열에 추가
            zonesDelete.value.push(targetFeatureId);
            console.log(targetFeatureId);
        },
        featureSelected: async function (event) {
            console.log('Feature 클릭 이벤트발생');

            let featArray = event.target.getFeatures().getArray(); //Select 객체의 Feature 배열
            let selectedFlag = true; //Select 이벤트 정상여부 Flag

            //Overlay 초기화
            await this.featureOverlayInit();

            //SELECT 배열을 순회하며 오버레이,클러스터링을 고려해 이벤트 처리
            for (var i = 0; i < featArray.length; i++) {
                let geomType = featArray[i].getGeometry().getType();
                let geomId = featArray[i].getId();

                if (geomType == 'Polygon') {
                    console.log('Polygon action');
                    let polygonDom = document.querySelector('#polygon_ovl_' + geomId);

                    if (!polygonDom.classList.contains('active')) {
                        polygonDom.classList.add('active');
                        polygonDom.style.display = 'block';
                    }
                } else if (geomType == 'LineString') {
                    console.log('LineString action');
                    let linStringDom = document.querySelector('#lineString_ovl_' + geomId);

                    if (!linStringDom.classList.contains('active')) {
                        linStringDom.classList.add('active');
                        linStringDom.style.display = 'block';
                    }
                } else if (geomType == 'Circle') {
                    console.log('Circle action');
                    let circleDom = document.querySelector('#circle_ovl_' + geomId);

                    if (!circleDom.classList.contains('active')) {
                        circleDom.classList.add('active');
                        circleDom.style.display = 'block';
                    }
                } else if (geomType == 'Point') {
                    console.log('Point action Start');

                    if (featArray[i].get('features').length > 1) {
                        //선택된 피쳐에 여러개의 피쳐가 있을경우 클러스터링으로 판단하여 선택 불가
                        alert('클러스터링 되어있습니다. 지도를 확대하여 선택해 주세요.');

                        event.target.getFeatures().remove(featArray[i]); //이미 선택된 피쳐 삭제
                        selectedFlag = false;
                    } else if (featArray[i].get('features')[0].getId() == undefined) {
                        //선택된 피쳐 배열에 ID 값이 없을경우 같은 피쳐 선택으로 판단하여 선택 불가
                        selectedFlag = false;
                    } else {
                        //선택된 포인트 피쳐의 길이를 하나로 판단하여 선택 가능 및 오버레이 동작
                        let clickMe = document.querySelectorAll('.overlay-wrap.point');

                        featArray[i].get('features').forEach(function async(geomVal) {
                            console.log(geomVal);
                            clickMe.forEach(function (domValue) {
                                //선택된 피쳐 순회
                                if (domValue.classList.contains('active') == false) {
                                    let domId = domValue.id; //포인트 오버레이 iD
                                    let geomId = geomVal.getId(); //선택된 피쳐 ID

                                    if (domId == 'point_ovl_' + geomId) {
                                        domValue.classList.add('active');
                                        domValue.style.display = 'block';
                                    } else {
                                        domValue.classList.remove('active');
                                        domValue.style.display = 'none';
                                    }
                                }
                            });
                        });

                        selectedFlag = true;
                    }
                }
            }

            modifyEnabled.value = false;
            if (event.selected.length > 0) {
                modifyEnabled.value = true;
                console.log(selectedFlag + '  수정가능 : ' + modifyEnabled.value + ' 선택된 피쳐 갯수 : ' + event.selected.length);
            }

            selectedFeatures.value = event.target.getFeatures();
            /*
            if (selectedFlag == true && event.selected.length > 0) {
                //수정가능 AND selectFeature 배열에 선택된 feature 추가
                console.log('수정가능 조건 통과');
            } else {
                //수정 불가
                console.log('수정불가');
                //modifyEnabled.value = false;

                //selectedFeatures.value = event.target.getFeatures();
            }
            */
        },
        /* Overlay Wrap 초기화 */
        featureOverlayInit: async function (geomType) {
            console.log('Overlay Style Init : ' + geomType);
            //let clickMe = geomType == null ? document.querySelectorAll('[id*=_ovl_]') : document.querySelectorAll('[id*=' + geomType + '_ovl_]');
            let clickMe = document.querySelectorAll('[id*=_ovl_]');

            clickMe.forEach(function (value, index) {
                if (value != null) {
                    clickMe[index].style.display = 'none';
                    clickMe[index].classList.remove('active');
                }
            });
        },
        /* Point Geometry Clustering Function */
        overrideStyleFunction: function (feature, style) {
            //console.log({ feature, style, resolution });
            console.log('Clustering Action');

            const clusteredFeatures = feature.get('features');
            const size = clusteredFeatures.length;
            style.getText().setText(size.toString());

            let overlays = document.querySelectorAll('.overlay-wrap.point');

            /*
            console.log('-------------------ZONE POINT------------------');
            zonesPoint.value.forEach(function (feature) {
                //console.log(feature.getGeometry());
                clusteredFeatures.forEach(function (clustFeature) {
                    if (feature.getId() == clustFeature.getId()) {
                        console.log('일치');
                        feature.setGeometry(clustFeature.getGeometry());
                    }
                });
                //console.log(feature.getGeometry());
            });
            console.log('-------------------ZONE POINT------------------');
            */

            //클러스터링 여부에 따라 오버레이 비활성화/활성화
            if (size > 1) {
                overlays.forEach(function (value, index) {
                    if (value != null) {
                        overlays[index].classList.remove('active');
                    }
                });
            } else {
                overlays.forEach(function (value, index) {
                    if (value != null) {
                        overlays[index].classList.add('active');
                    }
                });
            }
        },
        setBoardDelete: async function () {
            if (confirm('글을 삭제하시겠습니까?')) {
                const result = await this.$axios({
                    method: 'get',
                    url: '/api/setGeomBoard',
                    params: {
                        boardSq: this.boardSq,
                        actionType: 'delete',
                    },
                });

                if (result.status === 200) {
                    //삭제완료 후 리스트로 이동
                    this.getBoardList();
                }
            }
        },
        getBoardList: async function () {
            this.$router.push({
                name: 'boardList',
            });
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
    background-image: url('@/assets/close.png');
    background-size: 8px;
    background-repeat: no-repeat;
    background-color: #fff;
    background-position: center;
    padding: 8px 8px;
    font-size: 12px;
    border-radius: 10px;
    cursor: pointer;
}
.overlay-content-org {
    background: #efefef;
    box-shadow: 0 5px 10px rgb(2 2 2 / 20%);
    padding: 10px 20px;
    font-size: 16px;
}
</style>
