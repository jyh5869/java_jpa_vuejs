package com.example.java_jpa_vuejs.tensorFlow.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class levenUtil {

    
    // 입력한 단어와 가장 유사한 단어를 찾는 메서드
    public static String findMostSimilarWord(String inputWord, List<String> wordList) {
        int minDistance = Integer.MAX_VALUE;
        String mostSimilarWord = "";

        // 입력한 단어와 학습된 데이터의 각 단어를 비교하여 레벤슈타인 거리를 계산
        for (String word : wordList) {
            int distance = levenshteinDistance(inputWord, word);
            if (distance < minDistance) {
                minDistance = distance;
                mostSimilarWord = word;
            }
        }

        return mostSimilarWord;
    }


    // 레벤슈타인 거리 계산 메서드
    public static int levenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }


    // 최솟값 계산 메서드
    public static int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }


    // 입력한 단어와 유사한 상위 n개의 단어를 찾는 메서드
    public static List<String> findTopSimilarWords(String inputWord, List<String> wordList, int n) {
        Map<String, Integer> wordDistances = new HashMap<>();

        // 각 단어의 레벤슈타인 거리를 계산하여 저장
        for (String word : wordList) {
            int distance = levenshteinDistance(inputWord, word);
            wordDistances.put(word, distance);
        }

        // 거리가 가장 짧은 상위 n개의 단어를 추출하여 반환
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordDistances.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        List<String> topWords = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sortedEntries.size()); i++) {
            topWords.add(sortedEntries.get(i).getKey());
        }

        return topWords;
    }
}
