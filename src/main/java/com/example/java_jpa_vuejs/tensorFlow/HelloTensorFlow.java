package com.example.java_jpa_vuejs.tensorFlow;


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
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;
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
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
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

//https://wiki.yowu.dev/ko/Knowledge-base/Spring-Boot/Learning/095-building-a-machine-learning-system-with-spring-boot-and-tensorflow 

//https://github.com/tensorflow/tensorflow/tree/master/tensorflow/java/src

//https://github.com/tensorflow/java/#tensorflow-version-support      버전관리

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloTensorFlow {

    final private static Logger LOG = Logger.getGlobal();

    private final TensorDataService tensorDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    static int ROW = 10000;
	static int FEATURE = 0;


     /**
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
	 * csv 파일 데이터를 List<RoadDTO> 형식으로 전환
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
	 * csv 파일의 행/열 사이즈 측정
	 * @param filePath
	 * @throws IOException
	 */
	public static void getDataSize(String filePath) throws IOException {
		try {
			//read csv data file
			File csv = new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			String[] field = null;
			
			while((line=br.readLine())!=null) {
				field = line.split(",");
				ROW++;
			}
			
			FEATURE = field.length;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * csv 파일 데이터를 행렬로 옮김
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static void csvToMtrx(String filePath, float[][] mtrx) throws IOException {
		try {
			//read csv data file
			File csv = new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			String[] field = null;
			
			for(int i=0; i<mtrx.length; i++) {
				if((line=br.readLine())!= null) {
					field = line.split(",");
                    System.out.println(field.toString());
					for(int j=0; j<field.length; j++) {
						mtrx[i][j] = Float.parseFloat(field[j]);
					}
                    System.out.println(mtrx);
				}
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 행렬 값 확인용 출력
	 * @param mtrx
	 */
	public static void printMatrix(float[][] mtrx) {
		System.out.println("============ARRAY VALUES============");
		for(int i=0; i<mtrx.length; i++) {
			if(i==0)
				System.out.print("[");
			else
				System.out.println();
			for(int j =0; j<mtrx[i].length; j++) {
				System.out.print("["+mtrx[i][j]+"]");
			}
		}
		System.out.println("]");
	}


	private static INDArray fconvertToNdArray(float[] array) {
        return Nd4j.create(array);
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




































  /**
    * @method Tensor Flow Test( 텍스트 분석 모델 정상동작 샘플 코드 )
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr")
    public Map<String, Object> index123123123(@Valid PaginationDto paginationDto) throws Exception {
        String filePathTxt = "C:/Users/all4land/Desktop/testeng.txt";
        String filePathCsv = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";
        String charsetName = "EUC-KR"; // 파일의 인코딩에 맞게 수정
        // 엑셀 파일에서 과일 리스트 읽어오기
        //List<RoadDTO> fruitList = csvToRoadToList(filePathCsv);
        
        // 문장 이터레이터 생성
        SentenceIterator iter = new BasicLineIterator(new File(filePathTxt));

        List<String> tokens = new ArrayList<>();
        try {

            while (iter.hasNext()) {
                String line = iter.nextSentence();
                System.out.println("원본 문장: " + line);
                List<String> morphemes = analyzeKoreanText(line);
                
                System.out.println("형태소 분석 결과: " + StringUtils.join(morphemes, ", "));
                System.out.println();
                tokens.add(StringUtils.join(morphemes, " "));
            }
        } catch(Exception e) {
            e.printStackTrace();
            iter.finish();
        }
        
        CollectionSentenceIterator iterToken = new CollectionSentenceIterator(tokens);


        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        // Word2Vec 모델을 훈련시킵니다.
        WordVectors word2VecModel = trainWord2VecModel(iter, iterToken, tokenizerFactory);
    
        // 훈련된 모델을 사용하여 유사한 단어를 찾습니다.
        findSimilarWords(word2VecModel, "경기도 파주시 육창로길", 10);
    
        return null;
    }
    
    // 한글 텍스트의 형태소 분석을 수행하는 메서드
    public List<String> analyzeKoreanText(String text) {
        // 한글 텍스트를 형태소로 분석
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        
        // 형태소를 추출하여 리스트로 반환
        List<String> morphemes = new ArrayList<>();
        for (KoreanTokenizer.KoreanToken token : scala.collection.JavaConversions.seqAsJavaList(tokens)) {
            morphemes.add(token.text());
        }
        
        return morphemes;
    }

    // Word2Vec 모델을 훈련시키는 메서드
    @SuppressWarnings("deprecation")
    private static WordVectors trainWord2VecModel(SentenceIterator iter, CollectionSentenceIterator token, TokenizerFactory tokenizerFactory) {
        System.out.println("Load & Vectorize Sentences....");
        
        Word2Vec word2Vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(tokenizerFactory)
                .build();
    
        // 모델 학습
        word2Vec.fit();
    
        // 훈련된 Word2Vec 모델을 저장할 수 있습니다.
        try {
            WordVectorSerializer.writeWordVectors(word2Vec, "C:/Users/all4land/Desktop/korean_word2vec_model.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return word2Vec;
    }
    
    // 주어진 단어와 유사한 단어 찾기
    public static void findSimilarWords(WordVectors wordVectors, String targetWord, int n) {
        Collection<String> similarWords = wordVectors.wordsNearest(targetWord, n);
        System.out.println("주어진 단어 '" + targetWord + "'와 유사한 단어:");
        for (String word : similarWords) {
            System.out.println(word);
        }
    }



    /**
	 * List<RoadDTO> 형식의 데이터 생성 및 검증
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static List<RoadDTO> csvToRoadToList(String filePath) throws IOException {
		//cSV 파일을 Object 객체로 List에 적재
        try {
            int cnt = 0;
            
            //데이터 검증
            List<RoadDTO> data = readCsvToBean(filePath);
            Iterator<RoadDTO> it = data.iterator();
            while(it.hasNext()) {
                RoadDTO vo = (RoadDTO)it.next();

                System.out.println("num : "+ vo.getRn());


                cnt++;
                if(cnt == 100){break;};
            }

            return data;
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}


    // 엑셀 파일에서 과일 리스트를 읽어오는 메서드
    private static List<String> readExcel(String filename) throws Exception {
        List<String> fruitList = new ArrayList<>();
        FileInputStream file = new FileInputStream(filename);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
        Iterator<Row> rowIterator = sheet.iterator();

        int cnt = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cell = row.getCell(4); // 첫 번째 열의 셀 선택
            fruitList.add(cell.getStringCellValue());

            System.out.println(cell.getStringCellValue());

            cnt++;
            if(cnt == 1000){break;}
        }
        workbook.close();
        file.close();
        return fruitList;
    }









































    /**
    * @method Tensor Flow Test( 텍스트 분석 모델 정상동작 샘플 코드 )
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddrAction")
    public Map<String, Object> index1231231dfwe23(@Valid PaginationDto paginationDto) throws Exception {
        
        // 입력 키워드
        String inputWord = "경기도";
        String MODEL_PATH = "C:/Users/all4land/Desktop/korean_word2vec_model.txt";

        Map<String, Float[]> wordVectors = loadWordVectors(MODEL_PATH);
        // 학습된 단어와 해당 단어의 벡터 매핑을 생성합니다.

        // 입력 단어의 벡터를 가져옵니다.
        Float[] inputVector = wordVectors.get(inputWord);

        if (inputVector == null) {
            // 모델에 해당 단어의 벡터가 없는 경우에 대한 예외 처리
            System.out.println("입력한 단어에 대한 벡터가 모델에 존재하지 않습니다.");
            // 또는 예외를 던지거나 다른 처리를 수행할 수 있습니다.
            // 예를 들어, 디폴트 벡터 값을 사용하거나, 사용자에게 다른 입력을 요청할 수 있습니다.
        } else {
            // 입력 단어의 벡터가 존재하는 경우에는 정상적으로 계속 진행합니다.
            // 가장 유사한 단어를 찾습니다.
            //String mostSimilarWord = findMostSimilarWord(inputVector, wordVectors);
            //System.out.println("입력 단어와 가장 유사한 단어: " + mostSimilarWord);

            List<String> mostSimilarWord = findMostSimilarWordsTen(inputVector, wordVectors, 10);
            
            System.out.println("입력 단어와 가장 유사한 단어: ");
            for (String word : mostSimilarWord) {
                System.out.println(word);
            }
        }
        
        return null;
    }

    private static Map<String, Float[]> loadWordVectors(String filePath) throws IOException {
        Map<String, Float[]> wordVectors = new HashMap<>();
        int vectorSize = 100; // 벡터의 크기를 적절하게 설정하세요.
    
        // Word2Vec 모델 파일을 읽어와서 단어와 벡터 매핑을 생성합니다.
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            String[] parts = line.split(" ");
            String word = parts[0];
            Float[] vector = new Float[vectorSize]; // 벡터의 길이를 적절하게 설정하세요.
    
            // 벡터의 길이를 맞추기 위해 zero-padding을 적용합니다.
            for (int i = 1; i < Math.min(parts.length, vectorSize + 1); i++) {
                try {
                    vector[i - 1] = Float.parseFloat(parts[i]); // 각 부분을 Float로 변환합니다.
                } catch (NumberFormatException e) {
                    // 변환할 수 없는 경우에는 0으로 채웁니다.
                    vector[i - 1] = 0.0f;
                }
            }
            // 여기서부터 ***********
            if (vector.length < vectorSize) {
                Float[] paddedVector = new Float[vectorSize];
                System.arraycopy(vector, 0, paddedVector, 0, vector.length);
                wordVectors.put(word, paddedVector);
            } else {
                wordVectors.put(word, vector);
            }
            // 여기까지 ***********
        }
    
        return wordVectors;
    }


    private static List<String> findMostSimilarWordsTen(Float[] inputVector, Map<String, Float[]> wordVectors, int numSimilarWords) {
        // 유사도를 저장할 Map
        Map<String, Double> similarityMap = new HashMap<>();

        // 입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산합니다.
        for (Map.Entry<String, Float[]> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            Float[] vector = entry.getValue();

            // 입력값과 모델에 있는 단어들 간의 유사도를 계산합니다.
            double similarity = cosineSimilarity(inputVector, vector);

            // 유사도를 Map에 저장합니다.
            similarityMap.put(word, similarity);
        }

        // 유사도를 기준으로 내림차순으로 정렬합니다.
        List<Entry<String, Double>> sortedSimilarityList = new ArrayList<>(similarityMap.entrySet());
        sortedSimilarityList.sort(Entry.comparingByValue(Comparator.reverseOrder()));

        // 상위 numSimilarWords 개의 단어를 선택합니다.
        List<String> mostSimilarWords = new ArrayList<>();
        for (int i = 0; i < Math.min(numSimilarWords, sortedSimilarityList.size()); i++) {
            mostSimilarWords.add(sortedSimilarityList.get(i).getKey());
        }

        return mostSimilarWords;
    }


    private static String findMostSimilarWordOne(Float[] inputVector, Map<String, Float[]> wordVectors) {
        if (inputVector != null) {
            return "읍따"; // 입력된 단어가 모델에 있는 경우 해당 벡터 값을 반환합니다.
        }
        
        double maxSimilarity = Double.MIN_VALUE;
        String mostSimilarWord = null;
    
        // 입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산합니다.
        for (Map.Entry<String, Float[]> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            Float[] vector = entry.getValue();
    
            // 입력값과 모델에 있는 단어들 간의 유사도를 계산합니다.
            double similarity = cosineSimilarity(inputVector, vector);
    
            // 가장 유사도가 높은 단어를 업데이트합니다.
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarWord = word;
            }
        }

        return mostSimilarWord;
    }

    private static double cosineSimilarity(Float[] vectorA, Float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
    
        // 두 벡터의 길이가 같은지 확인합니다.
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vector lengths must be equal");
        }
    
        for (int i = 0; i < vectorA.length; i++) {
            if (vectorA[i] != null && vectorB[i] != null) {
                dotProduct += vectorA[i] * vectorB[i];
                normA += Math.pow(vectorA[i], 2);
                normB += Math.pow(vectorB[i], 2);
            } else {
                // 두 벡터의 해당 원소 중 하나가 null인 경우를 처리합니다.
                // 여기에서는 0으로 처리하도록 했습니다.
                dotProduct += 0;
                normA += 0;
                normB += 0;
            }
        }
    
        // 벡터의 크기가 0인 경우에는 코사인 유사도를 계산할 수 없습니다.
        if (normA == 0 || normB == 0) {
            return 0.0; // 혹은 다른 처리를 원하시면 수정 가능합니다.
        }
    
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    







































    // 입력한 단어와 가장 유사한 단어를 찾는 메서드
    public String findMostSimilarWord(String inputWord, List<String> wordList) {
        int minDistance = Integer.MAX_VALUE;
        String mostSimilarWord = "";

        // 입력한 단어와 학습된 데이터의 각 단어를 비교하여 레벤슈타인 거리를 계산
        for (String word : wordList) {
            int distance = levenshteinDistance(inputWord, word);
            if (distance < minDistance) {
                minDistance = distance;
                mostSimilarWord = word;
            }
        }

        return mostSimilarWord;
    }

    // 레벤슈타인 거리 계산 메서드
    public int levenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }

    // 최솟값 계산 메서드
    public int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    // 입력한 단어와 유사한 상위 n개의 단어를 찾는 메서드
    public List<String> findTopSimilarWords(String inputWord, List<String> wordList, int n) {
        Map<String, Integer> wordDistances = new HashMap<>();

        // 각 단어의 레벤슈타인 거리를 계산하여 저장
        for (String word : wordList) {
            int distance = levenshteinDistance(inputWord, word);
            wordDistances.put(word, distance);
        }

        // 거리가 가장 짧은 상위 n개의 단어를 추출하여 반환
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordDistances.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        List<String> topWords = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sortedEntries.size()); i++) {
            topWords.add(sortedEntries.get(i).getKey());
        }

        return topWords;
    }


    /**
    * @method Tensor Flow Test( 텍스트 분석 모델 정상동작 샘플 코드 )
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getLeven")
    public Map<String, Object> index123123123cvvf(@Valid PaginationDto paginationDto) throws Exception {
        String filePathTxt = "C:/Users/all4land/Desktop/test.txt";

        List<String> wordList = Arrays.asList("키위", "오렌지", "딸기");

        SentenceIterator iter = new BasicLineIterator(new File(filePathTxt));

        List<String> tokens = new ArrayList<>();
        try {

            while (iter.hasNext()) {
                String line = iter.nextSentence();

                tokens.add(line);
            }
        } catch(Exception e) {
            e.printStackTrace();
            iter.finish();
        }

        String inputWord = "범어길";
        String mostSimilarWord = findMostSimilarWord(inputWord, tokens);
        List<String> topSimilarWords = findTopSimilarWords(inputWord, tokens, 10);

        System.out.println("입력한 단어: " + inputWord);
        System.out.println("가장 유사한 단어: " + mostSimilarWord);

        System.out.println("상위 유사한 단어 10개:");
        for (String word : topSimilarWords) {
            System.out.println(word);
        }
        return null;
    }
}