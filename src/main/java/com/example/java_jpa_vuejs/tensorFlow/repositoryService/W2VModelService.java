package com.example.java_jpa_vuejs.tensorFlow.repositoryService;

import java.util.Collection;
import java.util.List;


public interface W2VModelService {

    Collection<String> getSimillarWords(String inputWord, int returnCnt, String leaningDataType);

    List<String> getCalculateDistance(String inputWord, Collection<String> result, int returnCnt);

    String getResultCode(Collection<String> mostSimilarWordMany);
}

