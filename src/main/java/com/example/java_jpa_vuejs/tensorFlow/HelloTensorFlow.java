package com.example.java_jpa_vuejs.tensorFlow;


import org.hibernate.id.enhanced.Optimizer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tensorflow.ConcreteFunction;
import org.tensorflow.Signature;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.op.Op;
import org.tensorflow.op.Ops;
import org.tensorflow.op.math.Add;
import org.tensorflow.op.math.Mean;
import org.tensorflow.op.math.Mul;
import org.tensorflow.op.train.ApplyGradientDescent;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TInt32;
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
import org.tensorflow.op.core.*;
import org.tensorflow.types.TFloat32;

import org.nd4j.linalg.factory.Nd4j;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

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
    
    static int ROW = 0;
	static int FEATURE = 0;


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
/* 
    private static Signature dbl(Ops tf) {

        Placeholder<TInt32> x = tf.placeholder(TInt32.DTYPE);
        Add<TInt32> dblX = tf.math.add(x, x);
        return Signature.builder().input("x", x).output("dbl", dblX).build();

    }
*/




    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr")
    public Map<String, Object> inde123242x(@Valid PaginationDto paginationDto) throws Exception {
		
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
        try (Graph graph = new Graph()) {
            Ops tf = Ops.create(graph);

            // 플레이스홀더 정의
			Placeholder<TFloat32> x = tf.placeholder(TFloat32.DTYPE);
            Placeholder<TFloat32> y = tf.placeholder(TFloat32.DTYPE);
            // 변수 정의
            Variable<TFloat32> weight = tf.variable(tf.constant(0.0f));
            Variable<TFloat32> bias = tf.variable(tf.constant(0.0f));

            // 모델 정의: y_pred = weight * x + bias
            Operand<TFloat32> yPred = tf.math.add(tf.math.mul(x, weight), bias);

            // 손실 함수 정의: Mean Squared Error
            Operand<TFloat32> loss = tf.math.mean(tf.math.squaredDifference(yPred, y), tf.constant(0));

            // 옵티마이저 정의: Gradient Descent
            float learningRate = 0.01f;
            TrainableOptimizer<TFloat32> optimizer = new TrainableOptimizer(tf, learningRate);
            //Operand<TFloat32> trainOp = optimizer.minimize(loss, tf.assign(weight, bias));
			
			// Gradient 계산
            //Assign<TFloat32> gradWeight = tf.assign(weight, tf.math.tan(tf.math.mul(tf.math.sub(yPred, y), x)));
            //Assign<TFloat32> gradBias = tf.assign(bias, tf.math.tan(tf.math.sub(yPred, y)));

			Assign<TFloat32> gradWeight = tf.assign(weight, tf.reduceSum(tf.math.tan(tf.math.mul(tf.math.sub(yPred, y), x)), tf.constant(0)));
			Assign<TFloat32> gradBias = tf.assign(bias, tf.reduceSum(tf.math.tan(tf.math.sub(yPred, y)), tf.constant(0)));

            // 텐서플로우 세션 시작
            try (Session session = new Session(graph)) {
                // 변수 초기화
                session.runner().addTarget(tf.init()).run();

                // 훈련 루프
                int numEpochs = 10;
                for (int epoch = 0; epoch < numEpochs; epoch++) {
                    for (int i = 0; i < xTrain.length; i++) {
                        float[] feedDict = {xTrain[i], yTrain[i]};
						// 학습 데이터
						float[] inputData = {1.0f, 2.0f, 3.0f};
						FloatNdArray inputndArray = NdArrays.vectorOf(1.0f, 2.0f, 3.0f);
						float[] labelData = {2.0f, 4.0f, 6.0f};
						FloatNdArray labelArray = NdArrays.vectorOf(2.0f, 4.0f, 6.0f);
						//NdArray labelArray = (NdArray) fconvertToNdArray(labelData);
						//Tensor<TFloat32> ttttt = Tensor.of(TFloat32.DTYPE, inputData);
						System.out.println("★★★★★★★★★★★★★★★");
						System.out.println(inputndArray);
						System.out.println(gradWeight);
						System.out.println("★★★★★★★★★★★★★★★");
						
						session.runner()
                        .addTarget(gradWeight)
                        .addTarget(gradBias)
                        .feed(x.asOutput(), TFloat32.tensorOf(inputndArray))
                        .feed(y.asOutput(), TFloat32.tensorOf(labelArray))
                        .run();
	 
                    }
                }

                // 최종 모델 파라미터 출력
                System.out.println("Final Weight: " + session.runner().fetch(gradWeight).run().get(0).expect(TFloat32.DTYPE));
                System.out.println("Final Bias: " + session.runner().fetch(gradBias).run().get(0).data());


				// 테스트 데이터
				float[] xTest = {6, 7, 8};
				float[] yTest = {13, 15, 17}; // 실제 정답 값

				// 테스트 루프
				for (int i = 0; i < xTest.length; i++) {
					float[] feedDict = {xTest[i]}; // 테스트 데이터에 대해 y에 대한 값을 제거
					FloatNdArray testInputArray = NdArrays.vectorOf(feedDict);

					// 모델 예측
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
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
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
}