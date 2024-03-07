package com.example.java_jpa_vuejs.tensorFlow;

import org.hibernate.graph.Graph;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tensorflow.ConcreteFunction;
import org.tensorflow.Signature;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.op.math.Add;
import org.tensorflow.types.TInt32;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;

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

        try (ConcreteFunction dbl = ConcreteFunction.create(HelloTensorFlow::dbl);
            Tensor<TInt32> x = TInt32.scalarOf(10);
            Tensor<TInt32> dblX = dbl.call(x).expect(TInt32.DTYPE)) {
            System.out.println(x.data().getInt() + " doubled is " + dblX.data().getInt());
        }

        retMap.put("list", retList);

        return retMap;
    }

    private static Signature dbl(Ops tf) {
        Placeholder<TInt32> x = tf.placeholder(TInt32.DTYPE);
        Add<TInt32> dblX = tf.math.add(x, x);
        return Signature.builder().input("x", x).output("dbl", dblX).build();
    }






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
            Sequential model = new Sequential();
            //model.add(new Dense(128, activation="relu", inputShape=(784,)));
            //model.add(new Dense(10, activation="softmax"));
        } catch (Exception e) {
            // TODO: handle exception
        }
		//저장된 모델 확인
		try(SavedModelBundle b = SavedModelBundle.load("/tmp/fromPython", "serve")){
			
            /* 
			//create a session from the Bundle
			Session sess = b.session();
			
			//create an input Tensor
			//Tensor x = Tensor.create(testInput);
			Tensor x = Tensor.of(null, null)

			//run the model and get the result
			float[][] y = sess.runner()
					.feed("x", x)
					.fetch("h")
					.run()
					.get(0)
					.copyTo(new float[ROW][1]);
			
			//print out the result
			for(int i=0; i<y.length;i++)
				System.out.println(y[i][0]);

            */
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

}