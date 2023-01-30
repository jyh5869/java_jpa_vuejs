package com.example.java_jpa_vuejs.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Members, Long> {

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
}