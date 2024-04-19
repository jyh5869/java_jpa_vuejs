package com.example.java_jpa_vuejs.tensorFlow.repositoryService;

import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;

public interface TensorDataService {

    Iterable<Roads> getAddrData();
    
}
