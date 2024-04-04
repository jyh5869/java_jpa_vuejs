package com.example.java_jpa_vuejs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.TimeZone;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;

import jakarta.annotation.PostConstruct;


@EnableJpaAuditing // Main method가 있는 클래스에 적용하여 JPA Auditing(감시, 감사) 기능을 활성화(@LastModifiedDate, @CreatedDate등을 사용하기 위함) 
@SpringBootApplication // (exclude = DataSourceAutoConfiguration.class) -> 데이터소스 자동 생성 제외 (Bean생성 안됨)
public class JavaJpaVuejsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaJpaVuejsApplication.class, args);
	}
	
    private static final Logger LOG = LoggerFactory.getLogger(JavaJpaVuejsApplication.class);
    
    /**
     * JPA Test Bean
     */
    @Bean
    public CommandLineRunner demo(MemberRepository repository) throws InterruptedException {
    
        long reqTime = Util.durationTime ("start", "JPA 테스트", 0, "Proceeding" );
        
        return (args) -> {
            try {             

                repository.findByEmail("5869jyh@hanmail.net");
                Util.durationTime ("end", "JPA 테스트", reqTime, "Complete" );
            }
            catch (Exception e) {
                
                Util.durationTime ("end", "JPA 테스트", reqTime, "Fail" );
                e.printStackTrace();
            }
        };
    }
/* 
    private final static String MODEL_PATH_WORD2VEC_BIN_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_bin_full.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_vec_full.vec";
    
    @Bean
    public WordVectors wordVectorsFull() {

        try {
            System.out.println("wordVectors - 모델 로드를 시작 합니다.");
            File modelFile = new File(MODEL_PATH_WORD2VEC_VEC_FULL);
            WordVectors word2VecModel = WordVectorSerializer.readWord2VecModel(modelFile, false); // 모델을 메모리에 로드
            //WordVectors word2VecModel = WordVectorSerializer.loadStaticModel(new File(VEC_PATH_WORD2VEC_VEC));//로드 오류 -로드 후 메소드 못찾음
            //WordVectors word2VecModel = WordVectorSerializer.loadStaticModel(new File(VEC_PATH_WORD2VEC_BIN));//로드 오류 -로드 후 메소드 못찾음
            //WordVectors word2VecModel = WordVectorSerializer.loadStaticModel(new File(MODEL_PATH_WORD2VEC_BIN));//로드 오류 - 모델 못찾음
            //WordVectors word2VecModel = WordVectorSerializer.loadStaticModel(new File(MODEL_PATH_WORD2VEC_VEC));//로드 오류 - 모델 못찾음

            //WordVectors word2VecModel = WordVectorSerializer.readWord2VecModel(new File(VEC_PATH_WORD2VEC_VEC), false); //정상 로드
            //WordVectors word2VecModel = WordVectorSerializer.readWord2VecModel(new File(VEC_PATH_WORD2VEC_BIN)); //정상 로드
            //WordVectors word2VecModel = WordVectorSerializer.readWord2VecModel(new File(MODEL_PATH_WORD2VEC_BIN)); //로드 오류 - 확장자 형식오류
            //WordVectors word2VecModel = WordVectorSerializer.readWord2VecModel(new File(MODEL_PATH_WORD2VEC_VEC)); //로드 오류 - 확장자 형식오류
           
            Collection<String> mostSimilarWordMany =  word2VecModel.wordsNearest("노원로28길", 20);

            System.out.println("주어진 단어 '" + "노원로28길" + "'와 유사한 단어 " + 20 + "개 출럭");
            for (String word : mostSimilarWordMany) {
                System.out.println(word);
            }
            System.out.println("모델이 성공적으로 로드되었습니다.");
            return word2VecModel;
        } 
        catch (Exception e) {
            System.err.println("모델을 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private final static String MODEL_PATH_WORD2VEC_BIN_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_bin_road.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_vec_road.vec";
    @Bean
    public WordVectors wordVectorsRoad() {

        try {
            System.out.println("wordVectorsRoad - 모델 로드를 시작 합니다.");
            File modelFile = new File(MODEL_PATH_WORD2VEC_VEC_ROAD);
            WordVectors word2VecModel = WordVectorSerializer.readWord2VecModel(modelFile, false); // 모델을 메모리에 로드
           
            Collection<String> mostSimilarWordMany =  word2VecModel.wordsNearest("노원로28길", 20);

            System.out.println("주어진 단어 '" + "노원로28길" + "'와 유사한 단어 " + 20 + "개 출럭");
            for (String word : mostSimilarWordMany) {
                System.out.println(word);
            }
            System.out.println("모델이 성공적으로 로드되었습니다.");
            return word2VecModel;
        } 
        catch (Exception e) {
            System.err.println("모델을 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
*/   
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }
}
