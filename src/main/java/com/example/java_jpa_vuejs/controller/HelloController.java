package com.example.java_jpa_vuejs.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {
    //https://jee-goo.tistory.com/entry/SpringBoot-%EA%B0%9C%EB%B0%9C-%ED%99%98%EA%B2%BD-%EA%B5%AC%EC%84%B1-BackEnd
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Hello, World!";
    }

    // http://localhost:8080/#/ vue 호출
    @RequestMapping(value = "/vue", method = RequestMethod.GET)
    public String index1() {
        return "/index";
    }

    //https://ohrora-developer.tistory.com/25
    //https://doozi0316.tistory.com/entry/Vuejs-Spring-Boot-MySQL-MyBatis-%EB%A7%9B%EC%A7%91-%EC%A7%80%EB%8F%84-%EB%A7%8C%EB%93%A4%EA%B8%B01-Spring-Boot-Vuejs-%EC%84%A4%EC%B9%98-%EB%B0%8F-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0
    @RequestMapping(value = "/api/hello", method = RequestMethod.GET)
    public String helloWorld() {
        return "hello!";
    }
    
}