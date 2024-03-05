package com.example.java_jpa_vuejs.tensorFlow.repositoryJPA;

import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoadRepository extends CrudRepository<Roads, Long> {

    Optional<Roads> findByRn(String email);

    Iterable<Roads> findAll();

    @Query(value =
            "SELECT * " +
            "FROM TN_SPRD_RDNM " +
            "limit 20", nativeQuery = true)
    Iterable<Roads> getAllRoads();
}