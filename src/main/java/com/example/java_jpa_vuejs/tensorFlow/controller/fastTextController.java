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
import org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils.WordSimilarity;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.fasttext.FastText;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.tensorFlow.common.word2VecUtil;
import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;
import com.example.java_jpa_vuejs.tensorFlow.service.W2VModelService;
import com.github.jfasttext.JFastText;
import com.github.jfasttext.FastTextWrapper;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import scala.collection.Seq;

import java.util.*;
import java.util.Map.Entry;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class fastTextController {

    static final  int ROW = 10000;
	
    private final int FEATURE = 0;
    private final int RETURN_COUNT = 50;
    private final static String MODEL_PATH_WORD2VEC_BIN_FULL = "C:/Users/all4land/Desktop/adress_fastText_bin_full.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_FULL = "C:/Users/all4land/Desktop/adress_fastText_vec_full";

    private final static String MODEL_PATH_WORD2VEC_BIN_ROAD = "C:/Users/all4land/Desktop/adress_fastText_bin_road.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_ROAD = "C:/Users/all4land/Desktop/adress_fastText_vec_road.vec";

    private final static String FILE_PATH_KOR_FULL = "documents/leaningData/addrkor_full.txt";
    private final static String FILE_PATH_KOR_ROAD = "documents/leaningData/addrkor_road.txt";

    private final static String FILE_PATH_KOR_TEST = "documents/leaningData/addrKorTest.txt";
    private final String FILE_PATH_ENG = "documents/leaningData/addrEng.txt";
    private final String FILE_PATH_KOR_CSV = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";

    private final W2VModelService w2VModelService;

    private final JFastText fastTextFullModel;
    private final Map<String, List<Float>> fastTextFullData;

    private final JFastText fastTextRoadModel;
    private final Map<String, List<Float>> fastTextRoadData;

    @Value("${model.fastText.analyzeYn}")
    private String ANALYZE_YN;
/**
 * 
supervised(true):

이 옵션은 FastText를 지도 학습 모드로 설정합니다.
즉, 문장 또는 문서와 그에 따른 레이블을 사용하여 모델을 학습시킬 때 사용됩니다.
지도 학습 모드에서는 단어 임베딩을 생성하는 대신, 텍스트 분류 작업과 같은 지도 학습 작업을 수행할 수 있습니다.
skipgram(true):

이 옵션은 FastText 모델의 학습 알고리즘을 skip-gram으로 설정합니다.
Skip-gram은 Word2Vec의 한 변형으로, 중심 단어 주변의 문맥 단어를 예측하는 방식으로 단어를 임베딩합니다.
Skip-gram 모델은 많은 양의 텍스트 데이터에서 단어 간 의미적 관계를 학습하는 데 유용합니다.


 */
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
            SentenceIterator iterator = new BasicLineIterator(FILE_PATH_KOR_FULL);

            // 토크나이저 설정 (토크나이저를 설정 하나 사용하지 않고 훈련)
            DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
            
            System.out.println("모델 학습을 시작하였습니다.");
            FastText fastText = FastText.builder()
                    .inputFile(FILE_PATH_KOR_FULL)//훈련데이터 경로
                    .outputFile(MODEL_PATH_WORD2VEC_VEC_FULL)//모델 저장 경로 설정
                    //.supervised(true) //supervised 모드로 설정
                    .skipgram(true)
                    .minCount(1) //최소 단어 빈도 설정
                    .epochs(5)
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

        System.out.println("학습된 모델로 분류 시작");
        try{
            if(analyzeType.equals("bin")){
                // FastText 모델 파일 경로
                String modelFilePath = MODEL_PATH_WORD2VEC_VEC_FULL+".bin"; // 모델 파일 경로로 수정

                System.out.println("모델 로드 시작 bin");
                FastText fastText = new FastText();
                fastText.loadBinaryModel(modelFilePath);
                
                //String similarAddresses = fastText.predict(inputWord); // 5개의 유사한 주소 검색
                //System.out.println(fastText.wordsNearest(inputWord, RETURN_COUNT));
                // double[] inputVector = fastText.getWordVector(inputWord);
                // fastText.getWordVectors(addrList);
                
                // 검색된 유사한 주소 출력
                //System.out.println("입력 주소: " + inputWord + " 가장 유사한 주소 " + similarAddresses);
            }
            else if(analyzeType.equals("vec")){
                // Word2Vec 모델 파일 경로
                String modelFilePath = MODEL_PATH_WORD2VEC_VEC_FULL+ ".vec";

                // Word2Vec 모델 로드
                System.out.println("모델 로드 시작 vec");
                WordVectors wordVectors = WordVectorSerializer.readWord2VecModel(new File(modelFilePath));
                //WordVectors wordVectors = WordVectorSerializer.readWordVectors(new File(modelFilePath));
                
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

            SentenceIterator iter = new BasicLineIterator(new File(FILE_PATH_KOR_FULL));

            // FastText 모델 설정
            FastText fastText = FastText.builder()
                    .inputFile(FILE_PATH_KOR_FULL)//훈련 데이터 - iterator와 동일기능으로 생략가능
                    .outputFile(MODEL_PATH_WORD2VEC_VEC_FULL)//모델 저장 경로 설정
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
            SentenceIterator iter = new BasicLineIterator(new File(FILE_PATH_KOR_FULL));

            try {
                while (iter.hasNext()) {
                    String line = iter.nextSentence();
                    //List<String> morphemes = analyzeKoreanText(line);
                    
                    //System.out.println("원본 문장: " + line + "   형태소 분석 결과: " + StringUtils.join(morphemes, ", "));
                    //addrList.add(StringUtils.join(morphemes, " "));
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
            trainWord2VecModel(tokenizedAddresses, MODEL_PATH_WORD2VEC_VEC_FULL);
            
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
        String refinementType = analyzeDTO.getRefinementType();

        System.out.println("모델 테스트를 시작합니다 JFastText");
        try{
            if(ANALYZE_YN.equals("Y")){
                //모델 로드
                System.out.println("모델 로드를 시작합니다 JFastText");

                //입력 주소
                String inputAddress = inputWord;

                List<Float> targetVector = fastTextFullModel.getVector(inputAddress);
                
                // 유사한 단어 찾기
                int numSimilarWords = 300; // 상위 유사한 단어의 개수


                List<String> similarWords = findMostSimilarWordMany(targetVector, fastTextFullData, numSimilarWords);
                // 결과 출력
                System.out.println("단어 '" + inputAddress + "'와 유사한 단어:");
                for (String word : similarWords) {
                    System.out.println(word);
                }

                //결과 데이터 정제(1: All, 2: Road) 및 단어 거리계산(형태가 유사한 단어 순 정렬)
                List<String> dataMiningResultMany = word2VecUtil.dataMiningFromResult(similarWords, refinementType);
                List<String> mostSimilarWordManyLev = w2VModelService.getCalculateDistance(inputWord, dataMiningResultMany, numSimilarWords);

                System.out.println("단어 '" + inputAddress + "'와 유사한 단어:");
                for (String word : mostSimilarWordManyLev) {
                    System.out.println(word);
                }

                System.out.println("모델 테스트가 완료되었습니다. JFastText");

                retMap.put("code", "SUCESS01");
                retMap.put("resuleMany", dataMiningResultMany);
                retMap.put("resuleManyLev", mostSimilarWordManyLev);
            }
            else{
                System.out.println("모델을 테스트 할 수 없는 환경에서 서버가 구동되었습니다.");

                retMap.put("code", "SUCESS02");
                retMap.put("resuleMany", null);
                retMap.put("resuleManyLev", null);
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

    public static List<String> findMostSimilarWordMany(List<Float> inputVector, Map<String, List<Float>> wordVectors, int numSimilarWords) {
        // 유사도를 저장할 Map
        Map<String, Double> similarityMap = new HashMap<>();

        // 입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산합니다.
        for (Map.Entry<String, List<Float>> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            List<Float> vector = entry.getValue();

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

    private static double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += Math.pow(vec1.get(i), 2);
            norm2 += Math.pow(vec2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
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

        // FastText 모델을 사용하여 Word2Vec 모델 훈련 skipgram supervised
        JFastText model = new JFastText(); 
        //model.runCmd(new String[]{"skipgram", "-input", FILE_PATH_KOR_FULL, "-output", modelPath, "-epoch", "25"});
        model.runCmd(new String[]{"skipgram","-input", FILE_PATH_KOR_FULL, "-output", modelPath, "-minCount", "1", "-epoch", "5", "-thread", "4", "-dim", "100", "-ws", "5", "-neg", "5", "-loss", "ns"});
    }


    /* 모델을 전달 받아 유사 단어를 호출 하는 함수 */
    private static List<String> findSimilarAddresses(JFastText model, String inputAddress, List<String> addresses) {
        // 입력 주소에 대한 토큰화
        List<String> inputTokens = tokenize(cleanText(inputAddress));

        // 입력 주소에 가장 유사한 주소 목록 검색
        List<String> similarAddresses = new ArrayList<>();
        List<String> mostSimilarWords = model.predict(StringUtils.join(inputTokens, " "), 5);

        // 유사한 단어를 포함하는 주소 찾기
        /* 
        for (String word : mostSimilarWords) {
            for (String address : addresses) {
                System.out.println(address);
                if (address.contains(word)) {
                    similarAddresses.add(address);
                }
            }
        }
        */
        return similarAddresses;
    }
}
