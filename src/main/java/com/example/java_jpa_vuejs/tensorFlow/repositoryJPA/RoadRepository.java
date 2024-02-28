package com.example.java_jpa_vuejs.tensorFlow.repositoryJPA;


import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RoadRepository extends CrudRepository<Roads, Long> {

    Optional<Roads> findByRn(String email);

    //Page<Roads> findAll(Pageable pageable);

    //Iterable<Roads> findAll();

    @Query(value =
            "SELECT * " +
            "FROM TN_SPRD_RDNM " +
            "limit 10", nativeQuery = true)
    Iterable<Roads> getAllRoads();

    //List<Roads> getAllRoads();
}


/* 

1. executeQuery()
ResultSet 반환
SELECT 에서 사용 -> @Query

2. executeUpdate()
해당되는 레코드의 개수를 int 값으로 반환
CREATE, DROP, ALTER, INSERT, UPDATE, DELETE 에서 사용 -> @Query, @Modifying, @Transactional

3. execute()
executeQuery, executeUpdate 둘 다 가능
boolean 값 반환

*/