package com.example.java_jpa_vuejs.tensorFlow.common;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;

import com.github.jfasttext.JFastText;

import scala.collection.Seq;

public class fastTextUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(fastTextUtil.class);

    private final static String FILE_PATH_KOR_FULL = "documents/leaningData/addrkor_full.txt";

    /* 데이터를 토큰화 하는 함수 */
    public static List<List<String>> preprocessAndTokenize(List<String> addresses) {
        List<List<String>> tokenizedAddresses = new ArrayList<>();
        for (String address : addresses) {
            String cleanedAddress = cleanText(address);
            List<String> tokens = tokenize(cleanedAddress);
            tokenizedAddresses.add(tokens);
        }
        return tokenizedAddresses;
    }


    /* 커스터마이징 전처리 함수 1 */
    private static String cleanText(String text) {
        //필요한 전처리 수행 (예: 특수 문자 제거, 공백 정리 등)
        return text;
    }


    /* 커스터마이징 전처리 함수 2 */
    private static List<String> tokenize(String text) {
        //형태소 분석기를 사용하여 토큰화
        return Arrays.asList(text.split("\\s+"));
    }


    /* 토큰화 시킨 데이터 파일로 JFast모델을 훈련하는 함수 */
    public static void trainWord2VecModel(List<List<String>> tokenizedData, String modelPath) {

        //토큰화된 데이터를 파일에 저장 (tokenized_data.txt 이렇게 입력하면 프로젝트 최상단 경로에 생성)
        try (PrintWriter writer = new PrintWriter("C:/Users/all4land/Desktop/tokenized_data.txt", "UTF-8")) {
            for (List<String> tokens : tokenizedData) {
                writer.println(StringUtils.join(tokens, " "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //FastText 모델을 사용하여 Word2Vec 모델 훈련 skipgram supervised
        JFastText model = new JFastText(); 
        //model.runCmd(new String[]{"skipgram", "-input", FILE_PATH_KOR_FULL, "-output", modelPath, "-epoch", "25"});
        model.runCmd(new String[]{"skipgram","-input", FILE_PATH_KOR_FULL, "-output", modelPath, "-minCount", "1", "-epoch", "5", "-thread", "4", "-dim", "100", "-ws", "5", "-neg", "5", "-loss", "ns"});
    }


    //한글 텍스트의 형태소 분석을 수행하는 메서드
    public List<String> analyzeKoreanText(String text) {
        //한글 텍스트를 형태소로 분석
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        
        //형태소를 추출하여 리스트로 반환
        List<String> morphemes = new ArrayList<>();
        for (KoreanTokenizer.KoreanToken token : scala.collection.JavaConversions.seqAsJavaList(tokens)) {
            morphemes.add(token.text());
        }
        
        return morphemes;
    }
}
