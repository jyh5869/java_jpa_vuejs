package com.example.java_jpa_vuejs.tensorFlow.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class tensorflowPython {
    
    private final static String pythonFilePath = "documents/python/send_data_to_java.py";
    private final static String pythonFilePath2 = "documents/python/keras_training_road_address.py";

    private static final Logger LOG = LoggerFactory.getLogger(tensorflowPython.class);
    
    /**
    * @method FastText를 이용하여 모델을 훈련하는 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/callTensorFlowTrainPython")
    public void callTensorFlowTrainPython (@Valid AnalyzeDTO analyzeDTO) throws Exception {

        try {
            // Python 스크립트 경로
            String pythonScriptPath = pythonFilePath2;

            // ProcessBuilder를 사용하여 Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath);
            Process process = pb.start();

            // 프로세스의 출력을 읽어오기 위한 BufferedReader 설정
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Python 스크립트에서 출력한 데이터 출력
                System.out.println("Received from Python: " + line);
            }

            // 프로세스가 완료될 때까지 대기하고 종료 코드를 가져오기
            int exitCode = process.waitFor();
            System.out.println("Python script execution completed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info("파이선 훈련 컨트롤러");
    }

    /**
    * @method FastText를 이용하여 모델을 훈련하는 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/callTensorFlowTestPython")
    public void callTensorFlowTestPython (@Valid AnalyzeDTO analyzeDTO) throws Exception {

        LOG.info("파이선 테스트 컨트롤러");
    }
}