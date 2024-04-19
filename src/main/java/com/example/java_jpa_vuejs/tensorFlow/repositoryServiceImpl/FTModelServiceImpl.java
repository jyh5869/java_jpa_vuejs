package com.example.java_jpa_vuejs.tensorFlow.repositoryServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.tensorFlow.common.levenUtil;
import com.example.java_jpa_vuejs.tensorFlow.common.word2VecUtil;
import com.example.java_jpa_vuejs.tensorFlow.repositoryService.FTModelService;
import com.github.jfasttext.JFastText;


import java.util.Comparator;
import java.util.Map.Entry;

import lombok.RequiredArgsConstructor;

@Service("fTModelService")
@RequiredArgsConstructor
public class FTModelServiceImpl implements FTModelService {

    @Value("${model.fastText.analyzeYn}")
    private String FT_ANALYZE_YN;//분석이 가능한 환경인지 아닌지

    private final JFastText fastTextFullModel;
    private final Map<String, List<Float>> fastTextFullData;

    private final JFastText fastTextRoadModel;
    private final Map<String, List<Float>> fastTextRoadData;


    /**
    * @method 메모리에 저장된 데이터를 사용하여 유사단어 추출
    * @param  null
    * @throws Exception
    */
    public List<String> getSimillarWords(String inputWord, int numSimilarWords, String leaningDataType){
        
        if(FT_ANALYZE_YN.equals("Y")){
            List<Float> targetVector = fastTextFullModel.getVector(inputWord);
        
            //유사도를 저장할 Map
            Map<String, Double> similarityMap = new HashMap<>();

            //입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산합니다.
            for (Map.Entry<String, List<Float>> entry : fastTextFullData.entrySet()) {
                String word = entry.getKey();
                List<Float> vector = entry.getValue();

                //입력값과 모델에 있는 단어들 간의 유사도를 계산합니다.
                double similarity = word2VecUtil.cosineSimilarity(targetVector, vector);

                //유사도를 Map에 저장합니다.
                similarityMap.put(word, similarity);
            }

            //유사도를 기준으로 내림차순으로 정렬합니다.
            List<Entry<String, Double>> sortedSimilarityList = new ArrayList<>(similarityMap.entrySet());
            sortedSimilarityList.sort(Entry.comparingByValue(Comparator.reverseOrder()));

            //상위 numSimilarWords 개의 단어를 선택합니다.
            System.out.println("단어 '" + inputWord + "'와 유사한 단어:");
            List<String> mostSimilarWords = new ArrayList<>();
            for (int i = 0; i < Math.min(numSimilarWords, sortedSimilarityList.size()); i++) {
                mostSimilarWords.add(sortedSimilarityList.get(i).getKey());
                System.out.println(sortedSimilarityList.get(i).getKey());
            }

            return mostSimilarWords;
        }
        else{
            return null;
        }
    }


    /**
    * @method 레벤슈타인 기법으로 단어간의 거리를 계산하여 정렬
    * @param  null
    * @throws Exception
    */
    @Override
    public List<String> getCalculateDistance(String inputWord, Collection<String> result , int returnCnt) {

        List<String> resultToList = new ArrayList<>();

        if(result != null){
            for (String word : result) {
                System.out.println(word);

                resultToList.add(StringUtils.join(word, " "));
            }
            
            List<String> mostSimilarWordMany = levenUtil.findTopSimilarWords(inputWord, resultToList, returnCnt);

            return mostSimilarWordMany;
        }
        else{
            return resultToList;
        }
    }


    /**
    * @method 분석결과 코드 세팅
    * @param  null
    * @throws Exception
    */
    @Override
    public String getResultCode(List<String> mostSimilarWordMany) {
        
        String resultCode;

        if(FT_ANALYZE_YN.equals("Y")){
            if(mostSimilarWordMany.size() != 0){
                resultCode = "SUCESS01";//분석가능한 상태에서 분석 결과가 있을때
            }
            else{
                resultCode = "SUCESS02";//분석가능한 상태에서 분석 결과가 없을때
            }
        }
        else{
            resultCode = "SUCESS03";//분석이 불가능한 상태일때
        }

        return resultCode;
    }
}
