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

//https://github.com/tensorflow/tensorflow/tree/master/tensorflow/java/src

//https://github.com/tensorflow/java/#tensorflow-version-support      버전관리

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloTensorFlow {

    final private static Logger LOG = Logger.getGlobal();

    private final TensorDataService tensorDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
    * @method 클라우드를 통한 리스트 호출
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

    static int ROW = 0;
	static int FEATURE = 0;




    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr")
    public Map<String, Object> inde123242x(@Valid PaginationDto paginationDto) throws Exception {
        System.out.println("TensorFlow version : "+TensorFlow.version());
		
		//String filePath = "./data/test.csv";
		String filePath = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";
        System.out.println("★ ★★★★★");
        System.out.println(filePath);

		//get shape of data
		getDataSize(filePath);
		System.out.print("[number of row] ==> "+ ROW);
		System.out.println(" / [number of feature] ==> "+ FEATURE);
		float[][] testInput = new float[ROW][FEATURE];
		
		//insert csv data to matrix
		csvToRoadObj(filePath, testInput, ROW);
        //csvToMtrx(filePath, testInput);
		//printMatrix(testInput);
		
		//load the model bundle
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
        return null;
    }


    /**
	 * csv 파일 데이터를 행렬로 옮김
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static void csvToRoadObj(String filePath, float[][] mtrx, int length) throws IOException {
		try {
			//read csv data file
			File csv = new File(filePath);
			BufferedReader reader = new BufferedReader(new FileReader(csv));
			String line = "";
			String[] field = null;
			RoadDTO[] roadArr = new RoadDTO[length];

            for(int i = 0; i < roadArr.length; i++) {
                roadArr[i] = new RoadDTO();
            }
            
            String[] joinDates = reader.readLine().split(",");
            System.out.println(reader.readLine());
            // i = 0 gives us the string 'joinDates' which is in the first column.
            // so we have to skip it and start it from i = 1
           
            for(int i = 1; i < joinDates.length; i++) {
                System.out.println(joinDates[i]);
                // setting the objects data..
                /* 
                roadArr[i - 1].setSigCd(joinDates[0]);
                roadArr[i - 1].setRnCd( joinDates[1]);
                roadArr[i - 1].setEmdNo( joinDates[2]);
                roadArr[i - 1].setRn( joinDates[3]);
                roadArr[i - 1].setEngNm( joinDates[4]);
                roadArr[i - 1].setSidoNm( joinDates[5]);
                roadArr[i - 1].setSggNm( joinDates[6]);
                roadArr[i - 1].setEmdSe( joinDates[7]);
                roadArr[i - 1].setEmdCd( joinDates[8]);
                roadArr[i - 1].setEmdNm( joinDates[9]);
                roadArr[i - 1].setUseYn( joinDates[10]);
                roadArr[i - 1].setAlwncResn( joinDates[11]);
                roadArr[i - 1].setAftchInfo( joinDates[12]);
                roadArr[i - 1].setCtpEngNm( joinDates[13]);
                roadArr[i - 1].setSigEngNm( joinDates[14]);
                roadArr[i - 1].setEmdEngNm( joinDates[15]);
                roadArr[i - 1].setBeginBsis( joinDates[16]);
                roadArr[i - 1].setEndBsis( joinDates[17]);
                roadArr[i - 1].setEffectDe( joinDates[18]);
                roadArr[i - 1].setErsrDe( joinDates[19]);
                roadArr[i - 1].setErsrDe( joinDates[20]);
                roadArr[i - 1].setErsrDe( joinDates[21]);
                 */
            }
           

		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
						//mtrx[i][j] = Float.parseFloat(field[j]);
                        mtrx[i][j] = field[j];
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