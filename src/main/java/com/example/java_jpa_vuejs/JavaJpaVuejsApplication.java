package com.example.java_jpa_vuejs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;
import com.github.jfasttext.JFastText;

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
    

    @Value("${model.word2Vec.analyzeYn}")
    private String WV_ANALYZE_YN;

    private final static String MODEL_PATH_WORD2VEC_BIN_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_bin_full.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_vec_full.vec";
    
    @Bean
    public WordVectors wordVectorsFull() {
        WordVectors word2VecModel = null;
        /*
         * 
         * 생성자에 null 리턴되서 서버가 안올라간당.. 어떻게 하면 좋을지 내일 생각 하자
         */
        try {
            if(WV_ANALYZE_YN.equals("Y")){
            
                System.out.println("wordVectors - 모델 로드를 시작 합니다.");
                File modelFile = new File(MODEL_PATH_WORD2VEC_VEC_FULL);
                word2VecModel = WordVectorSerializer.readWord2VecModel(modelFile, false); // 모델을 메모리에 로드
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
            else{
                System.out.println("Word2Vec Full Data - 모델을 로드하지 않는 환경으로 실행되었습니다.");
            }
        }
        catch (Exception e) {
            System.err.println("모델을 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return word2VecModel;
    }

    private final static String MODEL_PATH_WORD2VEC_BIN_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_bin_road.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_vec_road.vec";
    @Bean
    public WordVectors wordVectorsRoad() {

        WordVectors word2VecModel = null;

        try {
            if(WV_ANALYZE_YN.equals("Y")){
                System.out.println("wordVectorsRoad - 모델 로드를 시작 합니다.");
                File modelFile = new File(MODEL_PATH_WORD2VEC_VEC_ROAD);
                word2VecModel = WordVectorSerializer.readWord2VecModel(modelFile, false); // 모델을 메모리에 로드
            
                Collection<String> mostSimilarWordMany =  word2VecModel.wordsNearest("노원로28길", 20);

                System.out.println("주어진 단어 '" + "노원로28길" + "'와 유사한 단어 " + 20 + "개 출럭");
                for (String word : mostSimilarWordMany) {
                    System.out.println(word);
                }
                System.out.println("모델이 성공적으로 로드되었습니다.");
                return word2VecModel;
            }
            else{
                System.out.println("Word2Vec Full Data - 모델을 로드하지 않는 환경으로 실행되었습니다.");
            }
        } 
        catch (Exception e) {
            System.err.println("모델을 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return word2VecModel;
    }
  

    @Value("${model.fastText.analyzeYn}")
    private String FT_ANALYZE_YN;

    private final static String MODEL_PATH_FASTTEXT_BIN_FULL = "C:/Users/all4land/Desktop/adress_fastText_bin_full";
    private final static String MODEL_PATH_FASTTEXT_BIN_ROAD = "C:/Users/all4land/Desktop/adress_fastText_bin_road";
    
    @Bean
    public JFastText fastTextFullModel() {

        JFastText fastTextFull = new JFastText();

        try {
            if(FT_ANALYZE_YN.equals("Y")){

                System.out.println("FastText Full Model - 모델 로드를 시작 합니다.");
                
                fastTextFull.loadModel(MODEL_PATH_FASTTEXT_BIN_FULL+".bin");

                System.out.println("FastText Full Model - 모델이 성공적으로 로드되었습니다.");
                return fastTextFull;
            }
            else{
                System.out.println("FastText Full Model - 모델을 로드하지 않는 환경으로 실행되었습니다.");
            }
        }
        catch (Exception e) {
            System.err.println("FastText Full Model - 모델을 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return fastTextFull;
    }

    @Bean
    public Map<String, List<Float>> fastTextFullData() {

        JFastText fastTextFullModel = new JFastText();
        Map<String, List<Float>> fastTextFullData = new HashMap<>();

        try {
            if(FT_ANALYZE_YN.equals("Y")){
                System.out.println("FastText Full Data - 모델로부터 데이터 로드를 시작 합니다.");
                
                fastTextFullModel.loadModel(MODEL_PATH_FASTTEXT_BIN_FULL+".bin");

                // 모델의 단어 리스트 추출
                List<String> words = fastTextFullModel.getWords();
                
                
                for (String word : words) {
                    // 각 단어의 벡터 추출
                    List<Float> vector = fastTextFullModel.getVector(word);
                    fastTextFullData.put(word, vector);
                }

                System.out.println("FastText Full Data - 모델로 부터 데이터가 성공적으로 로드되었습니다.");
                return fastTextFullData;
            }
            else{
                System.out.println("FastText Full Data - 모델을 로드하지 않는 환경으로 실행되었습니다.");
            }
        } 
        catch (Exception e) {
            System.err.println("FastText Full Data - 모델로부터 데이터를 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return fastTextFullData;
    }


    @Bean
    public JFastText fastTextRoadModel() {

        JFastText fastTextFull = new JFastText();

        try {
            if(FT_ANALYZE_YN.equals("Y")){
                System.out.println("FastText Road Model - 모델 로드를 시작 합니다.");
                
                fastTextFull.loadModel(MODEL_PATH_FASTTEXT_BIN_ROAD+".bin");

                System.out.println("FastText Road Model - 모델이 성공적으로 로드되었습니다.");
                return fastTextFull;
            }
            else{
                System.out.println("FastText Road Model - 모델을 로드하지 않는 환경으로 실행되었습니다.");
            }
        } 
        catch (Exception e) {
            System.err.println("FastText Road Model - 모델을 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return fastTextFull;
    }

    @Bean
    public Map<String, List<Float>> fastTextRoadData() {

        JFastText fastTextFullModel = new JFastText();
        Map<String, List<Float>> fastTextFullData = new HashMap<>();

        try {
            if(FT_ANALYZE_YN.equals("Y")){
                System.out.println("FastText Road Data - 모델로부터 데이터 로드를 시작 합니다.");
                
                fastTextFullModel.loadModel(MODEL_PATH_FASTTEXT_BIN_ROAD+".bin");

                // 모델의 단어 리스트 추출
                List<String> words = fastTextFullModel.getWords();
                
                
                for (String word : words) {
                    // 각 단어의 벡터 추출
                    List<Float> vector = fastTextFullModel.getVector(word);
                    fastTextFullData.put(word, vector);
                }

                System.out.println("FastText Road Data - 모델로 부터 데이터가 성공적으로 로드되었습니다.");
                return fastTextFullData;
            }
            else{
                System.out.println("FastText Road Data - 모델을 로드하지 않는 환경으로 실행되었습니다.");
            }
        } 
        catch (Exception e) {
            System.err.println("FastText Road Data - 모델로부터 데이터를 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return fastTextFullData;
    }

    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }
}
