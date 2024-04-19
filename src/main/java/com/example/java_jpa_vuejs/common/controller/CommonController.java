package com.example.java_jpa_vuejs.common.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommonController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Map<String, Object>> index() throws InterruptedException, ExecutionException, IOException {

        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

        return result;
    }

    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    public String helloWorld() {
        return "hello world";
    }
}