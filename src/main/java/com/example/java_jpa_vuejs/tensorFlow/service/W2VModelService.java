package com.example.java_jpa_vuejs.tensorFlow.service;

import java.util.Collection;
import java.util.List;

import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;

public interface W2VModelService {
    double[] getWordVector(String word);

    Collection<String> getSimillarWords(String inputWord, int returnCnt, String leaningDataType);

    List<String> getCalculateDistance(String inputWord, Collection<String> result, int returnCnt);
}

