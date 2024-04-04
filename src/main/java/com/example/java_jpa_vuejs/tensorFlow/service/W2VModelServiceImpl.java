package com.example.java_jpa_vuejs.tensorFlow.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.tensorFlow.service.W2VModelService;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryJPA.GeometryBoardRepository;
import com.example.java_jpa_vuejs.tensorFlow.TensorDataService;
import com.example.java_jpa_vuejs.tensorFlow.common.levenUtil;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.validation.Empty;
import com.example.java_jpa_vuejs.tensorFlow.repositoryJPA.RoadsRepository;

import lombok.RequiredArgsConstructor;

@Service("w2VModelService")
@RequiredArgsConstructor
public class W2VModelServiceImpl implements W2VModelService {

	//private final WordVectors wordVectorsFull;
    //private final WordVectors wordVectorsRoad;

	// WordVectors 객체를 사용하는 메서드
    public double[] getWordVector(String word) {
        //return wordVectorsFull.getWordVector(word);
        return null;
    }

    public Collection<String> getSimillarWords(String inputWord, int returnCnt, String leaningDataType){
        /* 
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
            System.out.println("주어진 단어 '" + inputWord + "'와 유사한 단어 " + returnCnt + "개 출럭");
            for (String word : mostSimilarWordMany) {
                System.out.println(word);
            }
        }
        else{
            System.out.println("주어진 단어와 유사한 단어 추출이 실패하였습니다.");
        }

        return mostSimilarWordMany;
        */
        return null;
    }

    @Override
    public List<String> getCalculateDistance(String inputWord, Collection<String> result , int returnCnt) {

        List<String> resultToList = new ArrayList<>();//형태소 분석이 필요할 경우 형태소 분석후 데이터를 저장

        for (String word : result) {
            System.out.println(word);

            resultToList.add(StringUtils.join(word, " "));
        }
        
        List<String> mostSimilarWordMany11 = levenUtil.findTopSimilarWords(inputWord, resultToList, returnCnt);

        return mostSimilarWordMany11;
    }
    
}
