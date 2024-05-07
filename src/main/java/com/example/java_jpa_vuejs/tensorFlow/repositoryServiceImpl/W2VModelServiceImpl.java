package com.example.java_jpa_vuejs.tensorFlow.repositoryServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.tensorFlow.common.levenUtil;
import com.example.java_jpa_vuejs.tensorFlow.repositoryService.W2VModelService;

import lombok.RequiredArgsConstructor;

@Service("w2VModelService")
@RequiredArgsConstructor
public class W2VModelServiceImpl implements W2VModelService {

    @Value("${model.word2Vec.analyzeYn}")
    private String WV_ANALYZE_YN;//분석이 가능한 환경인지 아닌지

	private final WordVectors wordVectorsFull;
    private final WordVectors wordVectorsRoad;


    /**
    * @method 메모리에 저장된 모델을 호출하여 유사한 단어 추출
    * @param  null
    * @throws Exception
    */
    public Collection<String> getSimillarWords(String inputWord, int returnCnt, String leaningDataType){
        
        if(WV_ANALYZE_YN.equals("Y")){
            Collection<String> mostSimilarWordMany = null;
            INDArray array = null;

            if(leaningDataType.equals("FULL")){
                
                mostSimilarWordMany = wordVectorsFull.wordsNearest(inputWord, returnCnt);
                array = wordVectorsFull.getWordVectorsMean(mostSimilarWordMany);
            }
            else if(leaningDataType.equals("ROAD")){

                mostSimilarWordMany = wordVectorsRoad.wordsNearest(inputWord, returnCnt);
                wordVectorsRoad.getWordVectorsMean(mostSimilarWordMany);
            }
            
            System.out.println(array);

            if(mostSimilarWordMany != null){
                System.out.println("주어진 단어 '" + inputWord + "'와 유사한 단어 " + returnCnt + "개 출럭 [" + leaningDataType + "]");
                if(leaningDataType.equals("FULL")){
                    for (String word : mostSimilarWordMany) {
                        double similarity = wordVectorsFull.similarity(inputWord, word);
                        System.out.println("Similarity between \"" + inputWord + "\" and \"" + word + "\": " + similarity);
                    }
                }
                else if(leaningDataType.equals("ROAD")){
                    for (String word : mostSimilarWordMany) {
                        double similarity = wordVectorsRoad.similarity(inputWord, word);
                        System.out.println("Similarity between \"" + inputWord + "\" and \"" + word + "\": " + similarity);
                    }
                }
            }
            else{
                System.out.println("주어진 단어와 유사한 단어 추출이 실패하였습니다.");
            }
            return mostSimilarWordMany;
        }
        else{
            return null;
        }
    }


    /**
    * @method 레벤슈타인 기법으로 단어간 거리를 계산해 정렬
    * @param  null
    * @throws Exception
    */
    @Override
    public List<String> getCalculateDistance(String inputWord, Collection<String> result , int returnCnt) {

        List<String> resultToList = new ArrayList<>();//형태소 분석이 필요할 경우 형태소 분석후 데이터를 저장
        List<Map<String, String>> wordsMapList = new ArrayList<Map<String, String>>();

        String[] endings = {"로", "번길", "대로", "길"};

        if(result != null){
            for (String word : result) {
                String resultString;

                //System.out.println(word);

                //단어에서 숫자 제외
                resultString = word.replaceAll("\\d", "");
                //System.out.println(resultString);

                //단어에서 대로 로 길 제외
                for (String ending : endings) {

                    int lastIndex = resultString.lastIndexOf(ending);

                    if (lastIndex != -1) {
                        resultString = resultString.substring(0, lastIndex);

                        break;
                    }
                }
                
                Map<String, String> refineMap = new HashMap<String, String>();
                
                refineMap.put("origin", word);
                refineMap.put("refine", resultString);

                wordsMapList.add(refineMap);

                //resultToList.add(StringUtils.join(resultString, " "));
            }
            List<Map<String, String>> mostSimilarWordMany = levenUtil.findTopSimilarWordsRefine(inputWord, wordsMapList, returnCnt, "ASC");

            for (Map<String, String> similarWord : mostSimilarWordMany){
                resultToList.add(similarWord.get("origin"));
            }

            /* 
            List<String> mostSimilarWordMany = levenUtil.findTopSimilarWords(inputWord, resultToList, returnCnt);

            
            System.out.println("11111111111111111111111111");
            List<String> lastRefineResult = new ArrayList<>();//형태소 분석이 필요할 경우 형태소 분석후 데이터를 저장
            for (String similarWord : mostSimilarWordMany) {//정제 단어로 순위가 계산된 결고 리스트 순회
                System.out.println("222222222222222222222222222222222 similarWord : " + similarWord);
                for (Map<String, String> machingMap : machingToList) {//매칭 리스트에서 해당 단어와 동일한 원본 단어 확인
                    System.out.println("3333333333333333333333333333333333  : " + machingMap + " /////" + similarWord+"   /////   "+machingMap.get("refine"));
                    System.out.println(machingMap.get("refine"));

                    String machingWord = machingMap.get("refine").trim();
                    
                    if(machingWord.equals(similarWord.trim())){
                        lastRefineResult.add(machingMap.get("origin"));
                        System.out.println("☆ ☆ ☆ ☆ ☆ ☆ ☆ ☆  : " + machingMap.get("origin"));
                        break;
                    
                    }
                }
            }
            */
            return resultToList;
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
    public String getResultCode(Collection<String> mostSimilarWordMany) {
        
        String resultCode;

        if(WV_ANALYZE_YN.equals("Y")){
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
