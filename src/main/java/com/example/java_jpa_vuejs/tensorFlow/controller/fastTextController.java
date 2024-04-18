package com.example.java_jpa_vuejs.tensorFlow.controller;

import java.io.File;

import java.util.*;

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

import com.example.java_jpa_vuejs.tensorFlow.common.fastTextUtil;
import com.example.java_jpa_vuejs.tensorFlow.common.word2VecUtil;
import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;
import com.example.java_jpa_vuejs.tensorFlow.service.FTModelService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

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

    private final FTModelService fTModelService;

    /* 
    
    Deeplearning 모델 훈련 방식

    1.supervised(true):

    이 옵션은 FastText를 지도 학습 모드로 설정합니다.
    즉, 문장 또는 문서와 그에 따른 레이블을 사용하여 모델을 학습시킬 때 사용됩니다.
    지도 학습 모드에서는 단어 임베딩을 생성하는 대신, 텍스트 분류 작업과 같은 지도 학습 작업을 수행할 수 있습니다.
    
    2.skipgram(true):

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
    public Map<String, Object> getAnalyzeKeywordFastTextTrain (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();

        String inputWord = analyzeDTO.getInputKeyword();//입력 키워드
        String analyzeType = analyzeDTO.getAnalyzeType();//분석 타입 1.model, 2.vector
        String correctionYN = analyzeDTO.getCorrectionYN();//맞춤범 교정 여부

        try{
            System.out.println("모델 학습 과정을 시작합니다.");
            SentenceIterator iterator = new BasicLineIterator(FILE_PATH_KOR_FULL);

            //토크나이저 설정 (토크나이저를 설정 하나 사용하지 않고 훈련)
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

            //모델 훈련
            fastText.fit();

            retMap.put("code", "ERROR01");
            System.out.println("모델이 정상적으로 훈련되었습니다.");
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR04");
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
    public Map<String, Object> getAnalyzeKeywordFastTextTest (@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();//입력 키워드
        String analyzeType = analyzeDTO.getAnalyzeType();//분석 타입 1.model, 2.vector
        String correctionYN = analyzeDTO.getCorrectionYN();//맞춤범 교정 여부
        
        int numWords = 10;//검색할 유사한 단어의 수

        try{
            System.out.println("학습된 모델로 분류 시작");

            if(analyzeType.equals("bin")){
                //FastText 모델 파일 경로
                String modelFilePath = MODEL_PATH_WORD2VEC_VEC_FULL+".bin";//모델 파일 경로 세팅

                System.out.println("모델 로드 시작 bin");
                FastText fastText = new FastText();
                fastText.loadBinaryModel(modelFilePath);
                
                //분석된 유사주소 출력
                System.out.println("입력 주소: " + inputWord + " 가장 유사한 주소 ");
                for (String word : fastText.wordsNearest(inputWord,10)) {
                    System.out.println(word);
                }
            }
            else if(analyzeType.equals("vec")){
                // Word2Vec 모델 파일 경로
                String modelFilePath = MODEL_PATH_WORD2VEC_VEC_FULL+ ".vec";

                //Word2Vec 모델 로드
                System.out.println("모델 로드 시작 vec");
                WordVectors wordVectors = WordVectorSerializer.readWord2VecModel(new File(modelFilePath));
                //WordVectors wordVectors = WordVectorSerializer.readWordVectors(new File(modelFilePath));
                
                //유사한 단어 검색
                System.out.println("단어분석 시작 vec");
                Collection<String> similarWords = wordVectors.wordsNearest(inputWord, numWords);

                //검색된 유사단어 출력
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

        String analyzeType = analyzeDTO.getAnalyzeType();//분석 타입 1.model, 2.vector
        String correctionYN = analyzeDTO.getCorrectionYN();//맞춤범 교정 여부

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

            //모델 학습
            fastText.fit();
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR04");
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

        String analyzeType = analyzeDTO.getAnalyzeType();//분석 타입 1.model, 2.vector
        String correctionYN = analyzeDTO.getCorrectionYN();//맞춤범 교정 여부

        try{
            System.out.println("모델 학습 시작합니다 JFastText");

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

            //전처리 및 토큰화
            List<List<String>> tokenizedAddresses = fastTextUtil.preprocessAndTokenize(addrList);
            
            //Word2Vec 모델 훈련
            System.out.println("모델 훈련을 시작합니다. JFastText");
            fastTextUtil.trainWord2VecModel(tokenizedAddresses, MODEL_PATH_WORD2VEC_VEC_FULL);
            
            System.out.println("모델 훈련을 완료했습니다. JFastText");

            retMap.put("code", "SUCESS01");
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR04");
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

        String inputWord = analyzeDTO.getInputKeyword();//입력 키워드
        String analyzeType = analyzeDTO.getAnalyzeType();//분석 타입 1.model, 2.vector
        String correctionYN = analyzeDTO.getCorrectionYN();//맞춤범 교정 여부
        String refinementType = analyzeDTO.getRefinementType();//분석모델 타입

        int numSimilarWords = 300;//상위 유사한 단어의 개수

        try{
            System.out.println("모델 테스트를 시작합니다 JFastText");

            List<String> similarWords = fTModelService.getSimillarWords(inputWord, numSimilarWords, refinementType);
            //결과 데이터 정제(1: All, 2: Road) 및 단어 거리계산(형태가 유사한 단어 순 정렬)
            List<String> dataMiningResultMany = word2VecUtil.dataMiningFromResult(similarWords, refinementType);
            List<String> mostSimilarWordManyLev = fTModelService.getCalculateDistance(inputWord, dataMiningResultMany, numSimilarWords);

            String resultCode = fTModelService.getResultCode(similarWords);

            System.out.println("모델 테스트가 완료되었습니다. JFastText");

            retMap.put("code", resultCode);
            retMap.put("resuleMany", dataMiningResultMany);
            retMap.put("resuleManyLev", mostSimilarWordManyLev);
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR03");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }
}
