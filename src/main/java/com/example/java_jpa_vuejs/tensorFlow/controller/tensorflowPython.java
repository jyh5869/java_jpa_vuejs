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

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class tensorflowPython {
    
    /* 
     * 1. 전통적인 자연어 처리 방법론으로, 모델을 생성하지 않고 단순 통계적 기법을 이용해 문서 또는 텍스트 데이터를 벡터화. 
     * 2. 두 벡터 간의 유사도를 측정하기 위해 코사인 유사도(Cosine Similarity)를 사용하여, 각 텍스트가 얼마나 유사한지를 계산.
     * 3. TF-IDF는 단어의 빈도(Term Frequency, TF)와 역문서 빈도(Inverse Document Frequency, IDF)를 고려하여 각 단어의 중요도를 계산
     */
    private final static String MAKE_MODEL_TF_IDF_AND_COSINE = "documents/python/tf-idf_and_cosine_rada_model_make.py";// Tf-idf-Cosine
    private final static String USE_MODEL_TF_IDF_AND_COSINE = "documents/python/tf-idf_and_cosine_rada_model_use.py";// Tf-idf-Cosine
    private final static String MAKE_MODEL_KERAS = "documents/python/keras_model_make.py";// Deeplearning TensoFlow Keras
    private final static String USE_MODEL_KERAS = "documents/python/keras_model_use.py";// Deeplearning TensoFlow Keras

    private static final Logger LOG = LoggerFactory.getLogger(tensorflowPython.class);
    

    /**
    * @method TF-IDF AND COSINE를 이용한 모델 훈련 컨트롤러
    * @param  null    
    * @throws Exception
    */ 
    @GetMapping("/noAuth/tfIdfAndCosineRadaModelMake")
    public Map<String, Object> tfIdfAndCosineRadaModelMake (@Valid AnalyzeDTO analyzeDTO) throws Exception {

        LOG.info("파이썬 Tf-Idf And Cosine 모델 생성 START");

        int exitCode = 9999;// 모델 생성 스크립트 실행 결과 정수
        String exitCodeRes = null;// 모델 생성 스크립트 실행 결과 문자
        String inputKeyword = analyzeDTO.getInputKeyword();// 입력 키워드
        String defaultKeyword = analyzeDTO.getDefaultKeyword();// 디폴트 키워드

        Map<String, Object> retMap = new HashMap<String, Object>();
        
        try {
            String pythonScriptPath = MAKE_MODEL_TF_IDF_AND_COSINE;

            // ProcessBuilder를 사용하여 Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, inputKeyword, defaultKeyword);
            Process process = pb.start();

            // 프로세스의 출력을 읽어오기 위한 BufferedReader 설정
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Python 스크립트에서 출력한 데이터 출력
                System.out.println("Received from Python: " + line);
            }

            // 프로세스가 완료될 때까지 대기하고 종료 코드를 가져오기
            exitCode = process.waitFor();
            exitCodeRes = exitCode == 0 ? "SUCESS01" : "FAIL01";
            
            LOG.info("Python script execution completed with exit code: " + exitCode);

            retMap.put("code", exitCodeRes);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("파이썬 Tf-Idf And Cosine 모델 생성 END - 결과 : " + exitCodeRes + "[" +   String.valueOf(exitCode) +"]" );

        return retMap;
    }


    /**
    * @method TF-IDF AND COSINE를 이용한 모델 호출 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/tfIdfAndCosineRadaModelUse")
    public Map<String, Object> callTensorFlowTestPython (@Valid AnalyzeDTO analyzeDTO) throws Exception {

        LOG.info("파이썬 Tf-Idf And Cosine 모델 호출 START");

        int exitCode = 9999;// 모델 생성 스크립트 실행 결과 정수
        String exitCodeRes = null;// 모델 생성 스크립트 실행 결과 문자
        String inputKeyword = analyzeDTO.getInputKeyword();// 입력 키워드
        String defaultKeyword = analyzeDTO.getDefaultKeyword();// 디폴트 키워드

        Map<String, Object> retMap = new HashMap<String, Object>();
        
        try {
            // Python 스크립트 경로
            String pythonScriptPath = USE_MODEL_TF_IDF_AND_COSINE;

            // ProcessBuilder를 사용하여 Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, inputKeyword, defaultKeyword);
            Process process = pb.start();

            // 프로세스의 출력을 읽어오기 위한 BufferedReader 설정
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            
            String line;
            String lastLine = "";

            while ((line = reader.readLine()) != null) {
                // Python 스크립트에서 출력한 데이터 출력
                System.out.println("Received from Python: " + line);
                lastLine = line;
            }
            
            // 프로세스가 완료될 때까지 대기하고 종료 코드를 가져오기
            exitCode = process.waitFor();
            exitCodeRes = exitCode == 0 ? "SUCESS01" : "FAIL01";
            
            LOG.info("Python script execution completed with exit code: " + exitCode);
            
            output.append(lastLine);
            LOG.info("Analysis Test Result : " + output.toString());
            
             // JSON 문자열을 JSONObject로 파싱
            JSONObject analysisResult = JSONObject.fromObject(lastLine);

            // "top_similar_addresses" 배열을 가져옵니다.
            JSONArray resultMany = analysisResult.getJSONArray("top_similar_addresses");
            JSONArray resultManyLev = analysisResult.getJSONArray("sorted_addresses");
           
            LOG.info("Top Similar Addresses:");
            for (int i = 0; i < resultManyLev.size(); i++) {
                String address = resultManyLev.getString(i);
                LOG.info((i + 1) + ".    : " + address);
            } 

            retMap.put("code", exitCodeRes);
            retMap.put("searchkeyword", inputKeyword);
            retMap.put("resultMany", resultMany);
            retMap.put("resultManyLev", resultManyLev);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        LOG.info("파이썬 Tf-Idf And Cosine 모델 호출 END - 결과 : " + exitCodeRes + "[" +   String.valueOf(exitCode) +"]" );

        return retMap;
    }


    /**
    * @method KERAS를 이용한 모델 훈련 컨트롤러  
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/kerasModelMake")
    public Map<String, Object> kerasModelMake (@Valid AnalyzeDTO analyzeDTO) throws Exception {

        LOG.info("파이썬 Keras 모델 훈련 START");

        int exitCode = 9999;// 모델 생성 스크립트 실행 결과 정수
        String exitCodeRes = null;// 모델 생성 스크립트 실행 결과 문자
        String inputKeyword = analyzeDTO.getInputKeyword();// 입력 키워드
        String defaultKeyword = analyzeDTO.getDefaultKeyword();// 디폴트 키워드

        Map<String, Object> retMap = new HashMap<String, Object>();

        try {
            // Python 스크립트 경로
            String pythonScriptPath = MAKE_MODEL_KERAS;

            // ProcessBuilder를 사용하여 Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, inputKeyword, defaultKeyword);
            Process process = pb.start();

            // 프로세스의 출력을 읽어오기 위한 BufferedReader 설정
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Python 스크립트에서 출력한 데이터 출력
                System.out.println("Received from Python: " + line);
            }

            // 프로세스가 완료될 때까지 대기하고 종료 코드를 가져오기
            exitCode = process.waitFor();
            exitCodeRes = exitCode == 0 ? "SUCESS01" : "FAIL01";

            LOG.info("Python script execution completed with exit code: " + exitCode);

            retMap.put("code", exitCodeRes);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("파이썬 Keras 모델 훈련 END - 결과 : " + exitCodeRes + "[" +   String.valueOf(exitCode) +"]" );

        return retMap;
    }


    /**
    * @method KERAS를 이용한 모델 호출 컨트롤러
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/kerasModelUse")
    public Map<String, Object> kerasModelUse (@Valid AnalyzeDTO analyzeDTO) throws Exception {

        LOG.info("파이썬 Keras 모델 호출 START");

        int exitCode = 9999;// 모델 생성 스크립트 실행 결과 정수
        String exitCodeRes = null;// 모델 생성 스크립트 실행 결과 문자
        String inputKeyword = analyzeDTO.getInputKeyword();// 입력 키워드
        String defaultKeyword = analyzeDTO.getDefaultKeyword();// 디폴트 키워드

        Map<String, Object> retMap = new HashMap<String, Object>();

        try {
            // Python 스크립트 경로
            String pythonScriptPath = USE_MODEL_KERAS;

            // ProcessBuilder를 사용하여 Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, inputKeyword, defaultKeyword);
            Process process = pb.start();

            // 프로세스의 출력을 읽어오기 위한 BufferedReader 설정
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            String lastLine = "";
            while ((line = reader.readLine()) != null) {
                // Python 스크립트에서 출력한 데이터 출력
                System.out.println("Received from Python: " + line);
                lastLine = line;
            }

            // 프로세스가 완료될 때까지 대기하고 종료 코드를 가져오기
            exitCode = process.waitFor();
            exitCodeRes = exitCode == 0 ? "SUCESS01" : "FAIL01";
            LOG.info("Python script execution completed with exit code: " + exitCode);

            output.append(lastLine);
            LOG.info("Analysis Test Result : " + output.toString());
            
             // JSON 문자열을 JSONObject로 파싱
            JSONObject analysisResult = JSONObject.fromObject(lastLine);

            // "top_similar_addresses" 배열을 가져옵니다.
            JSONArray resultMany = analysisResult.getJSONArray("top_similar_addresses");
            JSONArray resultManyLev = analysisResult.getJSONArray("sorted_addresses");
           
            LOG.info("Top Similar Addresses:");
            for (int i = 0; i < resultManyLev.size(); i++) {
                String address = resultManyLev.getString(i);
                LOG.info((i + 1) + ".    : " + address);
            } 

            retMap.put("code", exitCodeRes);
            retMap.put("searchkeyword", inputKeyword);
            retMap.put("resultMany", resultMany);
            retMap.put("resultManyLev", resultManyLev);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("파이썬 Keras 모델 호출 END - 결과 : " + exitCodeRes + "[" +   String.valueOf(exitCode) +"]" );

        return retMap;
    }
}