package com.example.java_jpa_vuejs.auth2.repositoryJPA;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.java_jpa_vuejs.auth2.entity.Phone;

public interface PhoneRepository extends JpaRepository<Phone, Integer> {}
