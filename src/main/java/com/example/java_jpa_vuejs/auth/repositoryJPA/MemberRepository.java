package com.example.java_jpa_vuejs.auth.repositoryJPA;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.entity.Members;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface MemberRepository extends CrudRepository<Members, Long> {

    Optional<Members> findByEmail(String email);

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

    @Transactional
    @Modifying
    @Query(value =
            "INSERT INTO MEMBERS " + 
            "      (id ,email,  password,  name,  mobile,  nickname,  profile, is_deleted) " +
            "VALUES( (select Max(id) + 1 from MEMBERS A), :#{#regInfo.email}, :#{#regInfo.password}, :#{#regInfo.name}, :#{#regInfo.mobile}, :#{#regInfo.nickname}, :#{#regInfo.profile}, 'N') ", nativeQuery = true)
    void userRegistration(@Param(value = "regInfo") JoinDto joinDto);
}