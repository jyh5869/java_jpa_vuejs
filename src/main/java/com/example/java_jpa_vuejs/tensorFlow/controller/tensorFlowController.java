package com.example.java_jpa_vuejs.tensorFlow.controller;


import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.reduce3.CosineDistance;
import org.nd4j.linalg.api.ops.random.custom.RandomNormal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tensorflow.ConcreteFunction;
import org.tensorflow.Signature;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.internal.c_api.global.tensorflow;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.op.MathOps;
import org.tensorflow.op.Op;
import org.tensorflow.op.Ops;
import org.tensorflow.op.math.Add;
import org.tensorflow.op.math.Mean;
import org.tensorflow.op.math.Mul;
import org.tensorflow.op.math.Square;
import org.tensorflow.op.math.SquaredDifference;
import org.tensorflow.op.math.Sub;
import org.tensorflow.op.nn.Softmax;
import org.tensorflow.op.train.ApplyAdam;
import org.tensorflow.op.train.ApplyGradientDescent;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TInt32;
import org.tensorflow.types.TInt64;
import org.tensorflow.types.TString;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Graph;
import org.tensorflow.Operand;
import org.tensorflow.Output;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
//import org.tensorflow.Tensors;
import org.tensorflow.op.Ops;
import org.tensorflow.op.MathOps;
import org.tensorflow.op.core.*;
import org.tensorflow.op.linalg.MatMul;
import org.tensorflow.types.TFloat32;

import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.api.ndarray.INDArray;

//import org.renjin.cran.keras.model.Sequential;
//import org.tensorflow.keras.models.Sequential;

import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.tensorFlow.TensorDataService;
import com.example.java_jpa_vuejs.tensorFlow.common.hunspell;
import com.example.java_jpa_vuejs.tensorFlow.common.levenUtil;
import com.example.java_jpa_vuejs.tensorFlow.common.word2VecUtil;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;
import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;
import com.example.java_jpa_vuejs.tensorFlow.model.RoadDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.firebase.database.core.operation.Operation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.Duration;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.StringCleaning.*;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import dumonts.hunspell.Hunspell;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class tensorFlowController {

    private final Logger LOG = Logger.getGlobal();

    private final TensorDataService tensorDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    static final  int ROW = 10000;
	
    private final int FEATURE = 0;
    private final int RETURN_COUNT = 5;
    private final String MODEL_PATH = "C:/Users/all4land/Desktop/korean_word2vec_model.txt";
    private final String FILE_PATH_KOR = "documents/leaningData/addrkor.txt";
    private final String FILE_PATH_ENG = "documents/leaningData/addrEng.txt";
    /*
    * @method Tensor Flow Test( 텐서플로 정상 여부 체크 및 도로정보 테스트 데이터 10건 리턴)
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr12345")
    public Map<String, Object> index(@Valid PaginationDto paginationDto) throws Exception {

        String filePath = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";

		//학습 데이터의 현황(Col, Row) 파악
		//getDataSize(filePath);
		
		
		//insert csv data to matrix
		//List<RoadDTO> list = csvToRoadObj(filePath);


        String testKeyword = "양숭1길";

        //String[] fruits = {"노원로", "중앙로", "노원로28길", "성암로"};
        String[] fruits = csvToRoadObjReturnArr(filePath);
        System.out.println("[number of row] ==> "+ ROW + " / [number of feature] ==> "+ FEATURE + " / [FRUITS LENGTH ]  ==>" + fruits.length );
    
        // 학습 데이터 생성
        float[][] trainInputs = new float[][]{{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};

        try (Graph graph = new Graph(); Session session = new Session(graph)) {
            Ops tf = Ops.create(graph);

            // 입력 플레이스홀더
            org.tensorflow.op.core.Placeholder<TFloat32> x = tf.placeholder(TFloat32.DTYPE);

            // 가중치 및 편향 설정
            org.tensorflow.op.core.Variable<TFloat32> weights = tf.variable(tf.zeros(tf.constant(new long[]{ROW}), TFloat32.DTYPE));
            org.tensorflow.op.core.Variable<TFloat32> bias = tf.variable(tf.zeros(tf.constant(new long[]{ROW}), TFloat32.DTYPE));

            // 모델 정의
            Mul<TFloat32> matmul = tf.math.mul(x, weights);
            Add<TFloat32> yPred = tf.math.add(matmul, bias);

            // 텐서플로우 세션 시작
            session.runner().addTarget(tf.init()).run();

            // 모델 테스트

            float[] testInput = new float[ROW];//{0, 0, 0, 0};
            for (int i = 0; i < fruits.length; i++) {
                System.out.println("하위하위" + fruits[i] + "   /   " + i );
                if (fruits[i].equals(testKeyword)) {
                    testInput[i] = 1.0f;
                    break;
                }
            }
            FloatNdArray testInputArray = NdArrays.vectorOf(testInput);
            Tensor<TFloat32> testInputTensor = TFloat32.tensorOf(testInputArray);
            System.out.println("학습 시작");
            // 모델 테스트
            Tensor<TFloat32> predictionTensor = session.runner()
                    .fetch(yPred)
                    .feed(x.asOutput(), testInputTensor)
                    .run()
                    .get(0)
                    .expect(TFloat32.DTYPE);

            // 테스트 결과 출력
            float[] predictionValues = new float[ROW];
            FloatNdArray tensorData = predictionTensor.data();
            for (int i = 0; i < predictionValues.length; i++) {
                predictionValues[i] = tensorData.getFloat(i);
            }
            int predictedIndex = argmax(predictionValues);
            System.out.println("입력 키워드 '" + testKeyword + "'의 예측된 도로명: " + fruits[predictedIndex]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int argmax(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }




    /**
    * @method Tensor Flow Test( 텐서플로 정상 여부 체크 및 도로정보 테스트 데이터 10건 리턴)
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr1")
    public Map<String, Object> index1(@Valid PaginationDto paginationDto) throws Exception {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

        Iterable<Roads> list = tensorDataService.getAddrData();
        Iterator<Roads> itr = list.iterator();

        //리턴 리스트에 담기위한 형식변경
        int idx = 0;
        while(itr.hasNext()){ 
            idx++;
            Roads RoadEntity = itr.next();
            RoadDTO boardDto = RoadEntity.toDto(RoadEntity);
            System.out.println(idx + "      - "+boardDto.getRn());
            Map<String, Object> map = objectMapper.convertValue(boardDto, new TypeReference<Map<String, Object>>() {});

            retList.add(map);
        }

        System.out.println("Hello TensorFlow " + TensorFlow.version());
/* 
        try (ConcreteFunction dbl = ConcreteFunction.create(HelloTensorFlow::dbl);
            Tensor<TInt32> x = TInt32.scalarOf(10);
            Tensor<TInt32> dblX = dbl.call(x).expect(TInt32.DTYPE)) {
            System.out.println(x.data().getInt() + " doubled is " + dblX.data().getInt());
        }
*/
        retMap.put("list", retList);

        return retMap;
    }



    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr1133")
    public Map<String, Object> inde123242x(@Valid PaginationDto paginationDto) throws Exception {
        

        // 오타가 있는 키워드★★★
        String testKeyword = "사괴";

        String[] fruits = {"사과", "바나나", "오렌지", "딸기"};

        // TensorFlow 그래프 및 세션 생성
        try (Graph graph = new Graph(); Session session = new Session(graph)) {
            Ops tf = Ops.create(graph);

            // 플레이스홀더 정의
            Placeholder<TFloat32> x = tf.placeholder(TFloat32.DTYPE);

/* 
            // 변수 정의
            int numFruits = fruits.length;
            Variable<TFloat32> embeddings = tf.variable(tf.random.randomStandardNormal(
                    tf.constant(new long[]{numFruits, 3}),
                    TFloat32.DTYPE
            ));
*/
            // 변수 정의
            int numFruits = fruits.length;
            Variable<TFloat32> embeddings = tf.variable(tf.random.randomStandardNormal(
                    tf.constant(new long[]{numFruits}),
                    TFloat32.DTYPE
            ));

            // 훈련 데이터셋을 텐서로 변환
            float[][] trainInputs = new float[fruits.length][];
            float[][] trainLabels = new float[fruits.length][];
            for (int i = 0; i < fruits.length; i++) {
                float[] oneHot = new float[numFruits];
                oneHot[i] = 1.0f;
                trainInputs[i] = oneHot;
            }

            // 모델 정의
            Mul<TFloat32> mul = tf.math.mul(x, embeddings);
            Add<TFloat32> yPred = tf.math.add(mul, tf.constant(0.0f));

            // 손실 함수 정의: Mean Squared Error
            //Sub<TFloat32> sub = tf.math.sub(yPred, tf.oneHot(tf.dtypes.cast(x, TInt32.DTYPE), tf.constant(numFruits), tf.constant(1.0f), tf.constant(0.0f), -1));
            //Sub<TFloat32> sub = tf.math.sub(yPred, tf.oneHot(tf.math.argMax(x, tf.constant(0)), tf.constant(numFruits), tf.constant(1.0f), tf.constant(0.0f), OneHot.axis(-1)));
            Sub<TFloat32> sub = tf.math.sub(yPred, tf.oneHot(tf.math.argMax(x, tf.constant(0)), tf.constant(numFruits), tf.constant(1.0f), tf.constant(0.0f), OneHot.axis(-1L)));
            Mul<TFloat32> squaredDiff = tf.math.mul(sub, sub);
            //Mean<TFloat32> loss = tf.math.mean(squaredDiff, tf.constant(0));
            Mean<TFloat32> loss = tf.math.mean(squaredDiff, tf.constant(new int[] {}));

            // 옵티마이저 정의: Gradient Descent Optimizer
            float learningRate = 0.01f;
            Assign<TFloat32> optimizer = tf.assign(
                embeddings,
                tf.math.sub(
                    embeddings,
                    tf.math.mul(
                        tf.constant(learningRate),
                        tf.math.mean(
                            tf.math.mul(sub, x),
                            tf.constant(new int[] {0})  // 축을 지정하여 mean을 계산합니다.
                        )
                    )
                )
            );
            

            // 텐서플로우 세션 시작
            session.runner().addTarget(tf.init()).run();

            // 훈련 루프
            int numEpochs = 1000;
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                for (int i = 0; i < trainInputs.length; i++) {
                    float[] input = trainInputs[i];
                    float[] label = trainLabels[i];

                    FloatNdArray trainInput = NdArrays.vectorOf(input);

                    session.runner()
                            .addTarget(optimizer)
                            .feed(x.asOutput(), TFloat32.tensorOf(trainInput))
                            .run();
                }
            }

            // 테스트
            float[] testInput = new float[numFruits];
            for (int i = 0; i < fruits.length; i++) {
                if (fruits[i].equals(testKeyword)) {
                    testInput[i] = 1.0f;
                    break;
                }
            }

            FloatNdArray testInputArray = NdArrays.vectorOf(testInput);
            Tensor<TFloat32> testInputTensor = TFloat32.tensorOf(testInputArray);
            Tensor<TFloat32> tensor = session.runner()
                    .fetch(yPred)
                    .feed(x.asOutput(), testInputTensor)
                    .run()
                    .get(0)
                    .expect(TFloat32.DTYPE);


            org.tensorflow.ndarray.Shape shape = tensor.shape();
            long[] shapeArray = shape.asArray();
            float[] predictedValue = new float[(int) shapeArray[0]]; // 예측값 배열 초기화
 
/*             
            ByteBuffer byteBuffer = (ByteBuffer) tensor.rawData(); // ByteBuffer로 변환

            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer(); // FloatBuffer로 변환

            floatBuffer.get(predictedValue); // FloatBuffer에서 값 읽어와서 배열에 할당
            System.out.println(floatBuffer);

*/
            tensor.close(); // Tensor를 닫아줍니다.

            FloatNdArray tensorData = tensor.data();
            for (int i = 0; i < predictedValue.length; i++) {
                System.out.println("tensorData------------>"+ tensorData.toString());
                System.out.println("tensorData.getFloat(i) -------------->" +tensorData.getFloat(i));
                predictedValue[i] = tensorData.getFloat(i); // 텐서의 값을 배열에 복사
            }

            

            int predictedIndex = -1;
            float maxProb = Float.MIN_VALUE;
            System.out.println("maxProb -------------->" +maxProb);
            for (int i = 0; i < predictedValue.length; i++) {
                System.out.println("predictedValue[i] -------------->" +predictedValue[i]);
                if (predictedValue[i] > maxProb) {
                    predictedIndex = i;
                    maxProb = predictedValue[i];
                }
            }

            if(predictedIndex == -1){
                System.out.println("테스트 키워드 '" + testKeyword + "'의 예측 결과를 도출 할 수없습니다'");
            }
            else{
                System.out.println("테스트 키워드 '" + testKeyword + "'의 예측된 과일: " + fruits[predictedIndex]);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddrNumber")
    public Map<String, Object> inde123242xNumber(@Valid PaginationDto paginationDto) throws Exception {
		
		//학습 데이터 세팅을 위한 경로 
		String filePath = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";

		//학습 데이터의 현황(Col, Row) 파악
		getDataSize(filePath);
		System.out.println("[number of row] ==> "+ ROW + " / [number of feature] ==> "+ FEATURE);
		
		//insert csv data to matrix
		List<RoadDTO> list = csvToRoadObj(filePath);
        //csvToMtrx(filePath, testInput);
		//float[][] testInput = new float[ROW][FEATURE];
        //printMatrix(testInput);

		
		float[] xTrain = {1, 2, 3, 4, 5};
        float[] yTrain = {3, 5, 7, 9, 11};

        // TensorFlow 그래프 및 세션 생성
        try (Graph graph = new Graph(); Session session = new Session(graph)) {

            Ops tf = Ops.create(graph);

            // 플레이스홀더 정의
            Placeholder<TFloat32> x = tf.placeholder(TFloat32.DTYPE);
            Placeholder<TFloat32> y = tf.placeholder(TFloat32.DTYPE);

            // 변수 정의
            Operand<TFloat32> weight = tf.variable(tf.constant(0.0f));
            Operand<TFloat32> bias = tf.variable(tf.constant(0.0f));

            // 모델 정의: y_pred = weight * x + bias
            Operand<TFloat32> yPred = tf.math.add(tf.math.mul(x, weight), bias);

            // 손실 함수 정의: Mean Squared Error
            Operand<TFloat32> loss = tf.math.mean(tf.math.square(tf.math.sub(yPred, y)), tf.constant(0));

            // 옵티마이저 정의: Gradient Descent Optimizer
            float learningRate = 0.01f;
            Assign<TFloat32> optimizerWeight = tf.assign(
                    weight,
                    tf.math.sub(weight, tf.math.mul(tf.constant(learningRate), tf.math.mean(tf.math.mul(tf.math.sub(yPred, y), x), tf.constant(0))))
            );
            Assign<TFloat32> optimizerBias = tf.assign(
                    bias,
                    tf.math.sub(bias, tf.math.mul(tf.constant(learningRate), tf.math.mean(tf.math.sub(yPred, y), tf.constant(0))))
            );
            
            // 텐서플로우 세션 시작
            session.runner().addTarget(tf.init()).run();

            // 훈련 루프
            int numEpochs = 1000;
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                for (int i = 0; i < xTrain.length; i++) {
                    float[] feedDict = {xTrain[i], yTrain[i]};
                    FloatNdArray inputNdArray = NdArrays.vectorOf(feedDict[0]);
                    FloatNdArray labelArray = NdArrays.vectorOf(feedDict[1]);

                    session.runner()
                            .addTarget(optimizerWeight)
                            .addTarget(optimizerBias)
                            .feed(x.asOutput(), TFloat32.tensorOf(inputNdArray))
                            .feed(y.asOutput(), TFloat32.tensorOf(labelArray))
                            .run();
                }
            }

            // 최종 모델 파라미터 출력
            float finalWeight = session.runner().fetch(weight).run().get(0).expect(TFloat32.DTYPE).data().getFloat();
            float finalBias = session.runner().fetch(bias).run().get(0).expect(TFloat32.DTYPE).data().getFloat();
            System.out.println("Final Weight: " + finalWeight);
            System.out.println("Final Bias: " + finalBias);

            // 테스트 데이터
            float[] xTest = {6, 7, 8};
            float[] yTest = {13, 15, 17}; // 실제 정답 값

            // 테스트 루프
            for (int i = 0; i < xTest.length; i++) {
                float[] feedDict = {xTest[i]}; // 테스트 데이터에 대해 y에 대한 값을 제거
				FloatNdArray testInputArray = NdArrays.vectorOf(feedDict);

                float predictedValue = session.runner()
                        .fetch(yPred)
                        .feed(x.asOutput(), TFloat32.tensorOf(testInputArray))
                        .run()
                        .get(0)
                        .expect(TFloat32.DTYPE)
                        .data()
                        .getFloat();
                System.out.println("Test Input: " + xTest[i] + ", Actual Output: " + yTest[i] + ", Predicted Output: " + predictedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		//저장된 모델 확인
		try(SavedModelBundle b = SavedModelBundle.load("/tmp/fromPython", "serve")){
            
		}
        catch (Exception e) {
            // TODO: handle exception
            System.out.println("Modle Not Found Error !");
            //e.printStackTrace();
        }

        return null;
    }


    /**
	 * List<RoadDTO> 형식의 데이터 생성 및 검증
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static String[] csvToRoadObjReturnArr(String filePath) throws IOException {
		//cSV 파일을 Object 객체로 List에 적재
        try {
            int cnt = 0;

            //데이터 검증
            List<RoadDTO> data = readCsvToBean(filePath);
            Iterator<RoadDTO> it = data.iterator();

            String[] array_test3 = new String[ROW];

            while(it.hasNext()) {
                RoadDTO vo = (RoadDTO)it.next();
                array_test3[cnt] = vo.getRn();
                cnt++;
                if(cnt == ROW){break;};
            }

            return array_test3;
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}


    /**
	 * List<RoadDTO> 형식의 데이터 생성 및 검증
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static List<RoadDTO> csvToRoadObj(String filePath) throws IOException {
		//cSV 파일을 Object 객체로 List에 적재
        try {
            int cnt = 0;

            //데이터 검증
            List<RoadDTO> data = readCsvToBean(filePath);
            Iterator<RoadDTO> it = data.iterator();
            while(it.hasNext()) {
                RoadDTO vo = (RoadDTO)it.next();
                System.out.println("num : "+ vo.getSigCd());
                System.out.println("name : "+ vo.getRnCd());
                System.out.println("mobile : "+ vo.getEmdNo());
                System.out.println("num : "+ vo.getRn());
                System.out.println("name : "+ vo.getSigEngNm());
                System.out.println("mobile : "+ vo.getAlwncResn());

                cnt++;
                if(cnt == 10){break;};
            }

            return data;
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}


    /**
	 * CSV 파일 데이터를 List<RoadDTO> 형식으로 전환
	 * @param filePath
	 * @throws IOException
	 */
    public static List<RoadDTO> readCsvToBean(String filename){
		
		List<RoadDTO> data = null;
		try {
			//csv 파일 읽기
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename),"EUC-KR"),',','"',0);
			
			//CSV 를 VO에 매핑해주는 매퍼 역할을 할 클래스 객체 생성
			ColumnPositionMappingStrategy<RoadDTO> mapper = new ColumnPositionMappingStrategy<RoadDTO>();
			mapper.setType(RoadDTO.class);   //VO파일을 맵핑하겠다.
			String[] columns = new String[] {"sigCd","rnCd","emdNo","rn","engNm","sidoNm","sggNm","emdSe","emdCd","emdNm","useYn","alwncResn","chghy","aftchInfo","ctpEngNm","sigEngNm","emdEngNm","beginBsis","endBsis","effectDe","ersrDe"}; // 각 컬럼을 정의할 배열
			mapper.setColumnMapping(columns); //각 컬럼명을 매퍼에 설정
			
			//매핑하기!!
			CsvToBean<RoadDTO> csv = new CsvToBean<RoadDTO>();
			data = csv.parse(mapper, reader); //(매핑방법, csv파일)
			
			reader.close();
		}
        catch(Exception e) {
			e.getStackTrace();
		}
		
		return data;
	}


    /**
	 * CSV 파일의 행/열 사이즈 측정
	 * @param filePath
	 * @throws IOException
	 */
	@SuppressWarnings("null")
    public static int[] getDataSize(String filePath) throws IOException {
        
        int row = 0;
        int columns = 0;

		try {
			//CSV파일 읽어오기
			File csv = new File(filePath);
			try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
                String line = "";
                String[] field = null;
                
                while((line=br.readLine())!=null) {
                	field = line.split(",");
                	row++;
                }
                columns = field.length;
            }
		} 
        catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        return new int[]{row, columns};
	}
	




































































    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddrsdfsdfsdf")
    public Map<String, Object> inde123242xbak(@Valid PaginationDto paginationDto) throws Exception {
		
		//학습 데이터 세팅을 위한 경로 
		String filePath = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";

		//학습 데이터의 현황(Col, Row) 파악
		getDataSize(filePath);
		System.out.println("[number of row] ==> "+ ROW + " / [number of feature] ==> "+ FEATURE);
		
		//insert csv data to matrix
		List<RoadDTO> list = csvToRoadObj(filePath);
        //csvToMtrx(filePath, testInput);
		//float[][] testInput = new float[ROW][FEATURE];
        //printMatrix(testInput);
		
        try {
            //Sequential model = new Sequential();
            //model.add(new Dense(128, activation="relu", inputShape=(784,)));
            //model.add(new Dense(10, activation="softmax"));

        } catch (Exception e) {
            // TODO: handle exception
        }

		
		float[] xTrain = {1, 2, 3, 4, 5};
        float[] yTrain = {3, 5, 7, 9, 11};

        // TensorFlow 그래프 및 세션 생성
        try (Graph graph = new Graph(); Session session = new Session(graph)) {

            Ops tf = Ops.create(graph);

            // 플레이스홀더 정의
            Placeholder<TFloat32> x = tf.placeholder(TFloat32.DTYPE);
            Placeholder<TFloat32> y = tf.placeholder(TFloat32.DTYPE);

            // 변수 정의
            Operand<TFloat32> weight = tf.variable(tf.constant(0.0f));
            Operand<TFloat32> bias = tf.variable(tf.constant(0.0f));

            // 모델 정의: y_pred = weight * x + bias
            Mul<TFloat32> mul = tf.math.mul(x, weight);
            Add<TFloat32> yPred = tf.math.add(mul, bias);

            // 손실 함수 정의: Mean Squared Error
            Sub<TFloat32> sub = tf.math.sub(yPred, y);
            Mul<TFloat32> squaredDiff = tf.math.mul(sub, sub);
            Mean<TFloat32> loss = tf.math.mean(squaredDiff, tf.constant(0));
            
            
            float learningRate = 0.01f;
            ApplyGradientDescent<TFloat32> optimizerWeight = tf.train.applyGradientDescent(weight, tf.constant(learningRate), loss);
            ApplyGradientDescent<TFloat32> optimizerBias = tf.train.applyGradientDescent(bias, tf.constant(learningRate), loss);

			//Assign<TFloat32> optimizerWeight = tf.assign(weight, tf.reduceSum(tf.math.tan(tf.math.mul(tf.math.sub(yPred, y), x)), tf.constant(0)));
			//Assign<TFloat32> optimizerBias = tf.assign(bias, tf.reduceSum(tf.math.tan(tf.math.sub(yPred, y)), tf.constant(0)));
/*

             // 옵티마이저 정의: Adam Optimizer
            Operand<TFloat32> learningRate = tf.constant(0.001f);
            Operand <TFloat32> beta1 = tf.constant(0.9f);
            Operand<TFloat32> beta2 = tf.constant(0.999f);
            Operand <TFloat32> epsilon = tf.constant(1e-8f);

            Operand<TFloat32> m = tf.variable(tf.fill(tf.shape(weight), tf.constant(0.0f)));
            Operand<TFloat32> v = tf.variable(tf.fill(tf.shape(weight), tf.constant(0.0f)));
            Operand<TFloat32> beta1Power = tf.variable(tf.constant(1.0f));
            Operand<TFloat32> beta2Power = tf.variable(tf.constant(1.0f));


            Gradients gradientsWeight = Gradients.create(tf.scope(), Arrays.asList(loss), Arrays.asList(weight));
            ApplyAdam<TFloat32> optimizerWeight = tf.train.applyAdam(
                weight,        // 가중치 변수
                m,             // 첫 번째 모멘텀 지수
                v,             // 두 번째 모멘텀 지수
                beta1Power,    // 첫 번째 모멘텀의 지수의 승
                beta2Power,    // 두 번째 모멘텀의 지수의 승
                learningRate,  // 학습률
                beta1,         // 첫 번째 모멘텀 계수
                beta2,         // 두 번째 모멘텀 계수
                epsilon,       // 엡실론
                gradientsWeight.dy(0) // 그래디언트 (가중치 변수에 대한)
            );
            
            Gradients gradientsBias = Gradients.create(tf.scope(), Arrays.asList(loss), Arrays.asList(bias));
            ApplyAdam<TFloat32> optimizerBias = tf.train.applyAdam(
                bias,
                m,
                v,
                beta1Power,
                beta2Power,
                learningRate,
                beta1,
                beta2,
                epsilon,
                gradientsBias.dy(0)
            );
*/
            // 텐서플로우 세션 시작
            session.runner().addTarget(tf.init()).run();

            // 훈련 루프
            int numEpochs = 1;
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                for (int i = 0; i < xTrain.length; i++) {
                    float[] feedDict = {xTrain[i], yTrain[i]};
                    FloatNdArray inputNdArray = NdArrays.vectorOf(feedDict[0]);
                    FloatNdArray labelArray = NdArrays.vectorOf(feedDict[1]);

                    session.runner()
                            .addTarget(optimizerWeight)
                            .addTarget(optimizerBias)
                            .feed(x.asOutput(), TFloat32.tensorOf(inputNdArray))
                            .feed(y.asOutput(), TFloat32.tensorOf(labelArray))
                            .run();
                }
            }

            // 최종 모델 파라미터 출력
            float finalWeight = session.runner().fetch(weight).run().get(0).expect(TFloat32.DTYPE).data().getFloat();
            float finalBias = session.runner().fetch(bias).run().get(0).expect(TFloat32.DTYPE).data().getFloat();
            System.out.println("Final Weight: " + finalWeight);
            System.out.println("Final Bias: " + finalBias);

            // 테스트 데이터
            float[] xTest = {6, 7, 8};
            float[] yTest = {13, 15, 17}; // 실제 정답 값

            // 테스트 루프
            for (int i = 0; i < xTest.length; i++) {
                float[] feedDict = {xTest[i]}; // 테스트 데이터에 대해 y에 대한 값을 제거
				FloatNdArray testInputArray = NdArrays.vectorOf(feedDict);

                float predictedValue = session.runner()
                        .fetch(yPred)
                        .feed(x.asOutput(), TFloat32.tensorOf(testInputArray))
                        .run()
                        .get(0)
                        .expect(TFloat32.DTYPE)
                        .data()
                        .getFloat();
                System.out.println("Test Input: " + xTest[i] + ", Actual Output: " + yTest[i] + ", Predicted Output: " + predictedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		//저장된 모델 확인
		try(SavedModelBundle b = SavedModelBundle.load("/tmp/fromPython", "serve")){
            
		}
        catch (Exception e) {
            // TODO: handle exception
            System.out.println("Modle Not Found Error !");
            //e.printStackTrace();
        }

        return null;
    }








































    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddreeee")
    public Map<String, Object> inde123242wwwx(@Valid PaginationDto paginationDto) throws Exception {
        

        // 오타가 있는 키워드★★★
        String testKeyword = "오린지";

        String[] fruits = {"사과", "바나나", "오렌지", "딸기"};

        // TensorFlow 그래프 및 세션 생성
        try (Graph graph = new Graph(); Session session = new Session(graph)) {
            Ops tf = Ops.create(graph);

            // 플레이스홀더 정의
            Placeholder<TFloat32> x = tf.placeholder(TFloat32.DTYPE);

/* 
            // 변수 정의
            int numFruits = fruits.length;
            Variable<TFloat32> embeddings = tf.variable(tf.random.randomStandardNormal(
                    tf.constant(new long[]{numFruits, 3}),
                    TFloat32.DTYPE
            ));
*/
            // 변수 정의
            int numFruits = fruits.length;
            Variable<TFloat32> embeddings = tf.variable(tf.random.randomStandardNormal(
                    tf.constant(new long[]{numFruits}),
                    TFloat32.DTYPE
            ));

            // 훈련 데이터셋을 텐서로 변환
            float[][] trainInputs = new float[fruits.length][];
            float[][] trainLabels = new float[fruits.length][];
            for (int i = 0; i < fruits.length; i++) {
                float[] oneHot = new float[numFruits];
                oneHot[i] = 1.0f;
                trainInputs[i] = oneHot;
            }

            // 모델 정의
            Mul<TFloat32> mul = tf.math.mul(x, embeddings);
            Add<TFloat32> yPred = tf.math.add(mul, tf.constant(0.0f));

            // 손실 함수 정의: Mean Squared Error
            //Sub<TFloat32> sub = tf.math.sub(yPred, tf.oneHot(tf.dtypes.cast(x, TInt32.DTYPE), tf.constant(numFruits), tf.constant(1.0f), tf.constant(0.0f), -1));
            //Sub<TFloat32> sub = tf.math.sub(yPred, tf.oneHot(tf.math.argMax(x, tf.constant(0)), tf.constant(numFruits), tf.constant(1.0f), tf.constant(0.0f), OneHot.axis(-1)));
            Sub<TFloat32> sub = tf.math.sub(yPred, tf.oneHot(tf.math.argMax(x, tf.constant(0)), tf.constant(numFruits), tf.constant(1.0f), tf.constant(0.0f), OneHot.axis(-1L)));
            Mul<TFloat32> squaredDiff = tf.math.mul(sub, sub);
            //Mean<TFloat32> loss = tf.math.mean(squaredDiff, tf.constant(0));
            Mean<TFloat32> loss = tf.math.mean(squaredDiff, tf.constant(new int[] {}));

            // 옵티마이저 정의: Gradient Descent Optimizer
            float learningRate = 0.01f;
            Assign<TFloat32> optimizer = tf.assign(
                embeddings,
                tf.math.sub(
                    embeddings,
                    tf.math.mul(
                        tf.constant(learningRate),
                        tf.math.mean(
                            tf.math.mul(sub, x),
                            tf.constant(new int[] {0})  // 축을 지정하여 mean을 계산합니다.
                        )
                    )
                )
            );
            

            // 텐서플로우 세션 시작
            session.runner().addTarget(tf.init()).run();

            // 훈련 루프
            int numEpochs = 1000;
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                for (int i = 0; i < trainInputs.length; i++) {
                    float[] input = trainInputs[i];
                    float[] label = trainLabels[i];

                    FloatNdArray trainInput = NdArrays.vectorOf(input);

                    session.runner()
                            .addTarget(optimizer)
                            .feed(x.asOutput(), TFloat32.tensorOf(trainInput))
                            .run();
                }
            }

            // 테스트
            float[] testInput = new float[numFruits];
            for (int i = 0; i < fruits.length; i++) {
                if (fruits[i].equals(testKeyword)) {
                    testInput[i] = 1.0f;
                    break;
                }
            }

            FloatNdArray testInputArray = NdArrays.vectorOf(testInput);
            Tensor<TFloat32> testInputTensor = TFloat32.tensorOf(testInputArray);
            Tensor<TFloat32> tensor = session.runner()
                    .fetch(yPred)
                    .feed(x.asOutput(), testInputTensor)
                    .run()
                    .get(0)
                    .expect(TFloat32.DTYPE);


            org.tensorflow.ndarray.Shape shape = tensor.shape();
            long[] shapeArray = shape.asArray();
            float[] predictedValue = new float[(int) shapeArray[0]]; // 예측값 배열 초기화
 
/*             
            ByteBuffer byteBuffer = (ByteBuffer) tensor.rawData(); // ByteBuffer로 변환

            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer(); // FloatBuffer로 변환

            floatBuffer.get(predictedValue); // FloatBuffer에서 값 읽어와서 배열에 할당
            System.out.println(floatBuffer);

*/
            tensor.close(); // Tensor를 닫아줍니다.

            FloatNdArray tensorData = tensor.data();
            for (int i = 0; i < predictedValue.length; i++) {
                System.out.println("tensorData------------>"+ tensorData.toString());
                System.out.println("tensorData.getFloat(i) -------------->" +tensorData.getFloat(i));
                predictedValue[i] = tensorData.getFloat(i); // 텐서의 값을 배열에 복사
            }

            

            int predictedIndex = -1;
            float maxProb = Float.MIN_VALUE;
            System.out.println("maxProb -------------->" +maxProb);
            for (int i = 0; i < predictedValue.length; i++) {
                System.out.println("predictedValue[i] -------------->" +predictedValue[i]);
                if (predictedValue[i] > maxProb) {
                    predictedIndex = i;
                    maxProb = predictedValue[i];
                }
            }

            if(predictedIndex == -1){
                System.out.println("테스트 키워드 '" + testKeyword + "'의 예측 결과를 도출 할 수없습니다'");
            }
            else{
                System.out.println("테스트 키워드 '" + testKeyword + "'의 예측된 과일: " + fruits[predictedIndex]);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




































   

}