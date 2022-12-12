package com.example.java_jpa_vuejs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VueProxyTestController {

    @GetMapping("/hello1")
    public String helloWorld() {
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        return "hello!";
    }
}