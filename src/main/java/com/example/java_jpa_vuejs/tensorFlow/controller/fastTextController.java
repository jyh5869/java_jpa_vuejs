package com.example.java_jpa_vuejs.tensorFlow.controller;

import java.io.File;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.fasttext.FastText;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;
import com.github.jfasttext.JFastText;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class fastTextController {

    static final  int ROW = 10000;
	
    private final int FEATURE = 0;
    private final int RETURN_COUNT = 5;
    private final String MODEL_PATH = "C:/Users/all4land/Desktop/korean_word2vec_model";
    private final static String MODEL_PATH_FASTTEXT = "C:/Users/all4land/Desktop/korean_fastText_model";
    private final static String MODEL_PATH_FASTTEXT_VEC = "C:/Users/all4land/Desktop/korean_fastText_model.vec";
    private final static String FILE_PATH_KOR = "documents/leaningData/addrkor.txt";
    private final static String FILE_PATH_KOR_TEST = "documents/leaningData/addrKorTest.txt";
    private final String FILE_PATH_ENG = "documents/leaningData/addrEng.txt";


    /**
    * @method FastText를 이용하여 모델을 훈련하는 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordFastTextTrain")
    public Map<String, Object> getAnalyzeKeywordLinkedin23 (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();
        System.out.println("모델 학습 과정을 시작합니다.");
        try{

            // SentenceIterator 초기화 (데이터를 가져와 훈련하지않고 파일경로로 훈련)
            System.out.println("데이터를 가져오겠습니다.");
            SentenceIterator iterator = new BasicLineIterator(FILE_PATH_KOR);

            // 토크나이저 설정 (토크나이저를 설정 하나 사용하지 않고 훈련)
            DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
            
            System.out.println("모델 학습을 시작하였습니다.");
            FastText fastText = FastText.builder()
                    .inputFile(FILE_PATH_KOR)//훈련데이터 경로
                    .outputFile(MODEL_PATH_FASTTEXT)//모델 저장 경로 설정
                    .supervised(true) //supervised 모드로 설정
                    .build();

            // 훈련 데이터로 모델 훈련
            fastText.fit();

            System.out.println("모델이 정상적으로 훈련되었습니다.");
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR01");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }


    /**
    * @method FastText를 이용하여 훈련된 모델을 호출 및 테스트 하는 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordFastTextTest")
    public Map<String, Object> getAnalyzeKeywordLinkedin (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();
        String type = "bin";
        System.out.println("모델 학습 시작");
        try{
            if(type.equals("bin")){
                // FastText 모델 파일 경로
                String modelFilePath = "C:/Users/all4land/Desktop/korean_fastText_model.bin"; // 모델 파일 경로로 수정

                System.out.println("모델 로드 시작 bin");
                FastText fastText = new FastText();
                fastText.loadBinaryModel(modelFilePath);

                System.out.println("단어분석 시작 bin");
                String similarAddresses = fastText.predict(inputWord); // 5개의 유사한 주소 검색

                // 검색된 유사한 주소 출력
                System.out.println("입력 주소: " + inputWord + " 가장 유사한 주소 " + similarAddresses);
            }
            else if(type.equals("vec")){
                // Word2Vec 모델 파일 경로
                String modelFilePath = "C:/Users/all4land/Desktop/korean_fastText_model.vec";

                // Word2Vec 모델 로드
                System.out.println("모델 로드 시작 vec");
                WordVectors wordVectors = WordVectorSerializer.readWord2VecModel(new File(modelFilePath));

                // 유사한 단어 검색
                System.out.println("단어분석 시작 vec");
                int numWords = 5; // 검색할 유사한 단어의 수
                Collection<String> similarWords = wordVectors.wordsNearest(inputWord, numWords);

                // 검색된 유사한 단어 출력
                System.out.println("입력 단어: " + inputWord);
                System.out.println("가장 유사한 단어 목록:");
                for (String word : similarWords) {
                    System.out.println(word);
                }
            }
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR01");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }


    /**
    * @method FastText로 모델을 훈련하는 샘플코드 전체적인 파라메터의 의미및 과정
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordTrainSample")
    public Map<String, Object> getAnalyzeKeywordTrainSample (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        try{

            SentenceIterator iter = new BasicLineIterator(new File(FILE_PATH_KOR));

            // FastText 모델 설정
            FastText fastText = FastText.builder()
                    .inputFile(FILE_PATH_KOR)//훈련 데이터 - iterator와 동일기능으로 생략가능
                    .outputFile(MODEL_PATH_FASTTEXT)//모델 저장 경로 설정
                    //.iterator(iter)//훈련 데이터 - inputFile과 동일기능 으로 생략 가능 
                    .bucket(2000000) //버킷 크기 설정
                    .minCount(1) //최소 단어 빈도 설정
                    .wordNgrams(1) //단어 n-gram 설정
                    .minNgramLength(1)//최소 n-gram 길이 설정
                    .maxNgramLength(2)//최대 n-gram 길이 설정
                    .samplingThreshold(0) // 샘플링 임계값 설정
                    .learningRate(0.025)//학습률 설정
                    .dim(100)//임베딩 차원 설정
                    .contextWindowSize(5) // 문맥 창 크기 설정
                    .epochs(5)//에포크 수 설정
                    .negativeSamples(5)//네거티브 샘플 수 설정
                    .numThreads(20)//쓰레드 수 설정
                    //.skipgram(true)//Skipgram 모델로 설정(상황 따라 supervised과 선택적으로 사용)
                    .supervised(true)//지도(supervised) 모델로 설정(상황 따라 supervised과 선택적으로 사용)
                    //.predict(true)//예측(predict) 모델로 설정 ※ 예측 모델로 훈련하기 위해서는 train, label의 쌍 데이터가 필요하다
                    .build();

            //FastText 모델 학습
            fastText.fit();
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR01");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }


    /**
    * @method JFastText를 이용하여 모델을 훈련하는 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordJFastTrain")
    public Map<String, Object> getAnalyzeKeywordJFastTrain (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        System.out.println("모델 학습 시작합니다 JFastText");
        try{
            // 데이터 수집
            System.out.println("학습 데이터를 로드 합니다 JFastText");
            SentenceIterator iter = new BasicLineIterator(new File(FILE_PATH_KOR));

            try {
                while (iter.hasNext()) {
                    String line = iter.nextSentence();
                    addrList.add(line);
                }
            } 
            catch(Exception e) {
                e.printStackTrace();
                iter.finish();
            }
            
            // 전처리 및 토큰화
            List<List<String>> tokenizedAddresses = preprocessAndTokenize(addrList);
            
            // Word2Vec 모델 훈련
            System.out.println("모델 훈련을 시작합니다. JFastText");
            trainWord2VecModel(tokenizedAddresses, MODEL_PATH_FASTTEXT);
            
            System.out.println("모델 훈련을 완료했습니다. JFastText");

            retMap.put("code", "SUCESS01");
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR01");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }


    /**
    * @method JFastText를 이용하여 모델을 테스트하는 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordJFastTest")
    public Map<String, Object> getAnalyzeKeywordJFastTest (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        System.out.println("모델 테스트를 시작합니다 JFastText");
        try{
            
            //모델 로드
            System.out.println("모델 로드를 시작합니다 JFastText");
            //JFastText model = JFastText.loadFromFile(MODEL_PATH_FASTTEXT);
            JFastText model = new JFastText();
            model.loadModel(MODEL_PATH_FASTTEXT);

            //입력 주소
            String inputAddress = "서울특별시 강남구 역삼동 123-456";
            
            //입력 주소에 대한 유사한 주소 검색
            System.out.println("모델 테스트를 시작합니다 JFastText");
            List<String> mostSimilarWordMany = findSimilarAddresses(model, inputAddress, addrList);
            System.out.println("모델 테스트가 완료되었습니다. JFastText\"");

            System.out.println("입력 주소: " + inputAddress);
            System.out.println("가장 유사한 주소 목록:");
            for (String address : mostSimilarWordMany) {
                System.out.println(address);
            }

            retMap.put("code", "SUCESS01");
            retMap.put("resuleMany", mostSimilarWordMany);
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR01");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }


    /* 데이터를 토큰화 하는 함수 */
    private static List<List<String>> preprocessAndTokenize(List<String> addresses) {
        List<List<String>> tokenizedAddresses = new ArrayList<>();
        for (String address : addresses) {
            String cleanedAddress = cleanText(address);
            List<String> tokens = tokenize(cleanedAddress);
            tokenizedAddresses.add(tokens);
        }
        return tokenizedAddresses;
    }


    /* 커스터마이징 전처리 함수 1 */
    private static String cleanText(String text) {
        // 필요한 전처리 수행 (예: 특수 문자 제거, 공백 정리 등)
        // 이 예제에서는 단순히 공백을 기준으로 토큰화할 것이므로 특별한 전처리는 수행하지 않음
        return text;
    }


    /* 커스터마이징 전처리 함수 2 */
    private static List<String> tokenize(String text) {
        // 형태소 분석기를 사용하여 토큰화
        // 이 예제에서는 형태소 분석기를 사용하지 않으므로 단순히 공백을 기준으로 토큰화함
        return Arrays.asList(text.split("\\s+"));
    }


    /* 토큰화 시킨 데이터 파일로 JFast모델을 훈련하는 함수 */
    private static void trainWord2VecModel(List<List<String>> tokenizedData, String modelPath) {
        // 토큰화된 데이터를 파일에 저장 (tokenized_data.txt 이렇게 입력하면 프로젝트 최상단 경로에 생성)
        try (PrintWriter writer = new PrintWriter("C:/Users/all4land/Desktop/tokenized_data.txt", "UTF-8")) {
            for (List<String> tokens : tokenizedData) {
                writer.println(StringUtils.join(tokens, " "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // FastText 모델을 사용하여 Word2Vec 모델 훈련
        JFastText model = new JFastText();
        //model.runCmd(new String[]{"skipgram", "-input", "C:/Users/all4land/Desktop/tokenized_data.txt", "-output", modelPath, "-epoch", "25"});
        model.runCmd(new String[]{"supervised","-input", FILE_PATH_KOR, "-output", modelPath, "-minCount", "1", "-epoch", "5", "-thread", "4", "-dim", "100", "-ws", "5", "-neg", "5", "-loss", "ns"});
    }


    /* 모델을 전달 받아 유사 단어를 호출 하는 함수 */
    private static List<String> findSimilarAddresses(JFastText model, String inputAddress, List<String> addresses) {
        // 입력 주소에 대한 토큰화
        List<String> inputTokens = tokenize(cleanText(inputAddress));

        // 입력 주소에 가장 유사한 주소 목록 검색
        List<String> similarAddresses = new ArrayList<>();
        List<String> mostSimilarWords = model.predict(StringUtils.join(inputTokens, " "), 5);

        // 유사한 단어를 포함하는 주소 찾기
        for (String word : mostSimilarWords) {
            for (String address : addresses) {
                System.out.println(address);
                if (address.contains(word)) {
                    similarAddresses.add(address);
                }
            }
        }
        return similarAddresses;
    }
}
