package com.example.java_jpa_vuejs.tensorFlow.controller;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.java_jpa_vuejs.tensorFlow.common.hunspell;
import com.example.java_jpa_vuejs.tensorFlow.common.levenUtil;
import com.example.java_jpa_vuejs.tensorFlow.common.word2VecUtil;
import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;
import com.example.java_jpa_vuejs.tensorFlow.repositoryService.W2VModelService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.util.*;


@SuppressWarnings("deprecation")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class word2VecController {

    static final  int ROW = 10000;
	
    private final int FEATURE = 0;
    private final int RETURN_COUNT = 50;
    private final static String MODEL_PATH_WORD2VEC_BIN_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_bin_full.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_vec_full.vec";

    private final static String MODEL_PATH_WORD2VEC_BIN_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_bin_road.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_vec_road.vec";

    private final static String FILE_PATH_KOR_FULL = "documents/leaningData/addrkor_full.txt";
    private final static String FILE_PATH_KOR_ROAD = "documents/leaningData/addrkor_road.txt";

    private final static String FILE_PATH_KOR_TEST = "documents/leaningData/addrKorTest.txt";
    private final String FILE_PATH_ENG = "documents/leaningData/addrEng.txt";
    private final String FILE_PATH_KOR_CSV = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";

    private final W2VModelService w2VModelService;

    @Value("${model.word2Vec.analyzeYn}")
    private String ANALYZE_YN;//분석이 가능한 환경인지 아닌지


    /**
    * @method 텍스트를 입력받아 훈련된 백터 모델에서 코사인 유사도로 측정한 유사 도로명 추출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeyword")
    public Map<String, Object> getAnalyzeKeyword(@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();
        String leaningDataType = analyzeDTO.getLeaningDataType();
        
        System.out.println("Wor2Vec 모델로 텍스트 분석을 시작 합니다.");
        if(correctionYN.equals("Y")){
            inputWord = hunspell.spellingCorrection(inputWord);//맞춤법 및 문법 교정
        }
        
        //1. 생성되어있는 모델 호출하여 분석 , 2.모델의 리스트를 호출하여 코사인 유사도를 측정하여 분석
        if(analyzeType.equals("model")){
            
            Collection<String> mostSimilarWordMany = w2VModelService.getSimillarWords(inputWord, RETURN_COUNT, leaningDataType );
            
            List<String> mostSimilarWordManyLev = w2VModelService.getCalculateDistance(inputWord, mostSimilarWordMany, RETURN_COUNT );

            String resultCode = w2VModelService.getResultCode(mostSimilarWordMany);

            retMap.put("code", resultCode);
            retMap.put("resuleMany", mostSimilarWordMany);
            retMap.put("resuleManyLev", mostSimilarWordManyLev);
        }
        else{
            Map<String, Float[]> wordVectors = null;
            
            if(leaningDataType.equals("FULL")){
                wordVectors = word2VecUtil.loadWordVectorsToList(MODEL_PATH_WORD2VEC_BIN_FULL);//모델의 데이터셋 호출
            }
            else{
                wordVectors = word2VecUtil.loadWordVectorsToList(MODEL_PATH_WORD2VEC_BIN_ROAD);//모델의 데이터셋 호출
            }

            Float[] inputVector = wordVectors.get(inputWord);//입력키워드 백터화

            if (inputVector == null) {
                //모델에 해당 단어의 벡터가 없는 경우에 대한 예외 처리
                System.out.println("입력한 단어에 대한 벡터가 모델에 존재하지 않습니다.");
                
                retMap.put("code","ERROR01");
            } 
            else {
                //입력 단어의 벡터가 존재하는 경우에는 가장 유사한 10개의 단어를 찾는다.
                List<String> mostSimilarWordMany = word2VecUtil.findMostSimilarWordMany(inputVector, wordVectors, RETURN_COUNT);
                String mostSimilarWordOne = word2VecUtil.findMostSimilarWordOne(inputVector, wordVectors);
                
                System.out.println("입력 단어 분석 - START");
                System.out.println("입력된 단어'" + inputWord + "'와 가장 유사한 단어 : " + mostSimilarWordOne);
                System.out.println("입력된 단어'" + inputWord + "'와 가장 유사한 " + RETURN_COUNT + "개의 리스트 : " + mostSimilarWordMany.toString());
                for (String word : mostSimilarWordMany) {
                    System.out.println(word);
                }
                System.out.println("입력 단어 분석 - END");

                retMap.put("code", "SUCESS01");
                retMap.put("resuleOne", mostSimilarWordOne);
                retMap.put("resuleMany", mostSimilarWordMany);
            }
        }
        System.out.println("Wor2Vec 모델로 텍스트 분석이 완료 되었습니다.");
        
        retMap.put("analyzeType", analyzeType);
        retMap.put("correctionYN", correctionYN);

        return retMap;
    }

  
    /**
    * @method 레벤슈타인 기법으로 텍스트간의 거리를 측정해서 유사한 단어 추출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordLeven")
    public Map<String, Object> getAnalyzeKeywordLeven(@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        SentenceIterator iter = new BasicLineIterator(new File(FILE_PATH_KOR_ROAD));

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

        try {
            String mostSimilarWordOne = levenUtil.findMostSimilarWord(inputWord, addrList);
            List<String> mostSimilarWordMany = levenUtil.findTopSimilarWords(inputWord, addrList, 10);

            System.out.println("입력한 단어: " + inputWord + "와 가장 유사한 단어: " + mostSimilarWordOne);
            System.out.println("입력한 단어: " + inputWord + "와 상위 유사한 단어 10개:");
            for (String word : mostSimilarWordMany) {
                System.out.println(word);
            }

            retMap.put("code", "SUCESS01");
            retMap.put("resuleOne", mostSimilarWordOne);
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

    
    /**
    * @method Tensor Flow Test( 텍스트 분석 모델 정상동작 샘플 코드 )
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordWord2VecTrain")
    public Map<String, Object> getAnalyzeKeywordWord2VecTrain(@Valid AnalyzeDTO analyzeDTO) throws Exception {

        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String morphologicalYN = analyzeDTO.getMorphologicalYN();
        String leaningDataType = analyzeDTO.getLeaningDataType();

        System.out.println("모델 학습 과정을 시작합니다.");

        SentenceIterator iter = null;//문장 반복자
        List<String> morphoResult = new ArrayList<>();//형태소 분석결과를 저장할 리스트 

        if(leaningDataType.equals("FULL")){
            System.out.println("Set Full Adress Data");
            iter = new BasicLineIterator(new File(FILE_PATH_KOR_FULL));
        }
        else if(leaningDataType.equals("ROAD")){
            System.out.println("Set Road Adress Data");
            iter = new BasicLineIterator(new File(FILE_PATH_KOR_ROAD));
        }
        
        if(morphologicalYN.equals("Y")){
            try {
                while (iter.hasNext()) {
                    String line = iter.nextSentence();
                    List<String> morphemes = word2VecUtil.analyzeKoreanText(line);
                    
                    //System.out.println("원본 문장: " + line + "   형태소 분석 결과: " + StringUtils.join(morphemes, ", "));
                    morphoResult.add(StringUtils.join(morphemes, " "));
                }
            } catch(Exception e) {
                e.printStackTrace();
                iter.finish();
            }
        }
        
        CollectionSentenceIterator morphoResultIter = new CollectionSentenceIterator(morphoResult);

        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        //tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());//1.중요하지 않은 데이터를 압축 노원로28길 -> 노원로길
        tokenizerFactory.setTokenPreProcessor(null);//2.데이터 그대로를 학습 노원로28길 -> 노원로28길길

        InMemoryLookupCache cache = new InMemoryLookupCache();//메모리 기반의 구체적인 단어 조회 캐시: 램이 충분하고 작은데이터에 유리
        //AbstractCache<VocabWord> cache = new AbstractCache<>();//추상화된 캐시 클래스로 사용자가 캐시를 커스텀: 램이 부족하고 데이터가 방대할때 유리

        //Word2Vec 모델을 훈련
        WordVectors word2VecModel = word2VecUtil.trainWord2VecModel(iter, morphoResultIter, tokenizerFactory, cache, leaningDataType);

        //훈련된 모델을 사용하여 유사한 단어 추출
        Collection<String> mostSimilarWordMany = word2VecModel.wordsNearest(inputWord, 10);
        
        System.out.println("모델 테스트 과정을 진행합니다.");
        System.out.println("주어진 단어 '" + inputWord + "'와 유사한 단어:");
        for (String word : mostSimilarWordMany) {
            System.out.println(word);
        }
    
        return null;
    }
    

}
