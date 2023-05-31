package com.example.java_jpa_vuejs.auth.repositoryJPA;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.entity.Members;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface MemberRepository extends CrudRepository<Members, Long> {

    Optional<Members> findByEmail(String email);
    Optional<Members> findById(String id);    

    @Query(value =
            "select count(*) " +
            "from members " +
            "where email = :email ", nativeQuery = true)
    Integer countByEmail(@Param("email") String email);

    @Query(value =
            "select count(*) " +
            "from members " +
            "where mobile = :mobile ", nativeQuery = true)
    Integer countByMobile(@Param("mobile") String mobile);

    
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO MEMBERS " + 
            "      (id ,email,  password,  name,  mobile,  nickname,  profile, delete_yn) " +
            "VALUES( (select Max(id) + 1 from MEMBERS A), :#{#regInfo.email}, :#{#regInfo.password}, :#{#regInfo.name}, :#{#regInfo.mobile}, :#{#regInfo.nickname}, :#{#regInfo.profile}, 'N') ", nativeQuery = true)
    void userRegistration(@Param(value = "regInfo") JoinDto joinDto);
        
    @Query(value = "SELECT IFNULL(MAX(ID)+1, 0) FROM MEMBERS", nativeQuery = true)
    long getLastIdex();

    @Modifying
    @Transactional
    @Query(value = "UPDATE MEMBERS SET IS_DELETED = 'Y' WHERE ID = :deleteId ", nativeQuery = true)
    Integer setDeleteUser(@Param("deleteId") String deleteId);
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