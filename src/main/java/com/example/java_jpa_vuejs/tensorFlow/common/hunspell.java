package com.example.java_jpa_vuejs.tensorFlow.common;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import dumonts.hunspell.Hunspell;

public class hunspell {
    
    public static String spellingCorrection(String inputString) throws IOException {
        
        // hunspell 사전 파일 경로 설정
        Path dictionaryPath = Paths.get("documents/hunspell/ko.dic");
        Path affixPath = Paths.get("documents/hunspell/ko.aff");

        // Hunspell 객체 생성
        Hunspell hunspell = new Hunspell(dictionaryPath, affixPath);

        // 입력 문장
        String text = "한국어에는 맞춤법 오류가 있습니다.";

        // 문장을 공백을 기준으로 단어로 분할하여 배열로 저장
        String[] words = inputString.split("\\s+");

        // 각 단어에 대해 오타 교정 수행
        for (String word : words) {
            // 단어의 오타 교정 후보들을 배열로 반환
            String[] suggestions = hunspell.suggest(word);
            if (suggestions.length > 0) {
                // 첫 번째 교정 제안을 선택하여 출력
                String correctedWord = suggestions[0];
                System.out.println("원래 단어: " + word + ", 교정된 단어: " + correctedWord);
            } else {
                System.out.println("오타가 발견되지 않았습니다.");
            }
        }
        
        return words.toString();
    }
}
