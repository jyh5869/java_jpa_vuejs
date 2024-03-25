package com.example.java_jpa_vuejs.tensorFlow.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class word2VecUtil {
    
    public static List<String> findMostSimilarWordMany(Float[] inputVector, Map<String, Float[]> wordVectors, int numSimilarWords) {
        // 유사도를 저장할 Map
        Map<String, Double> similarityMap = new HashMap<>();

        // 입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산합니다.
        for (Map.Entry<String, Float[]> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            Float[] vector = entry.getValue();

            // 입력값과 모델에 있는 단어들 간의 유사도를 계산합니다.
            double similarity = cosineSimilarity(inputVector, vector);

            // 유사도를 Map에 저장합니다.
            similarityMap.put(word, similarity);
        }

        // 유사도를 기준으로 내림차순으로 정렬합니다.
        List<Entry<String, Double>> sortedSimilarityList = new ArrayList<>(similarityMap.entrySet());
        sortedSimilarityList.sort(Entry.comparingByValue(Comparator.reverseOrder()));

        // 상위 numSimilarWords 개의 단어를 선택합니다.
        List<String> mostSimilarWords = new ArrayList<>();
        for (int i = 0; i < Math.min(numSimilarWords, sortedSimilarityList.size()); i++) {
            mostSimilarWords.add(sortedSimilarityList.get(i).getKey());
        }

        return mostSimilarWords;
    }


    public static String findMostSimilarWordOne(Float[] inputVector, Map<String, Float[]> wordVectors) {
        if (inputVector != null) {
            return "읍따"; // 입력된 단어가 모델에 있는 경우 해당 벡터 값을 반환합니다.
        }
        
        double maxSimilarity = Double.MIN_VALUE;
        String mostSimilarWord = null;
    
        // 입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산합니다.
        for (Map.Entry<String, Float[]> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            Float[] vector = entry.getValue();
    
            // 입력값과 모델에 있는 단어들 간의 유사도를 계산합니다.
            double similarity = cosineSimilarity(inputVector, vector);
    
            // 가장 유사도가 높은 단어를 업데이트합니다.
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarWord = word;
            }
        }

        return mostSimilarWord;
    }

    private static double cosineSimilarity(Float[] vectorA, Float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
    
        // 두 벡터의 길이가 같은지 확인합니다.
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vector lengths must be equal");
        }
    
        for (int i = 0; i < vectorA.length; i++) {
            if (vectorA[i] != null && vectorB[i] != null) {
                dotProduct += vectorA[i] * vectorB[i];
                normA += Math.pow(vectorA[i], 2);
                normB += Math.pow(vectorB[i], 2);
            } else {
                // 두 벡터의 해당 원소 중 하나가 null인 경우를 처리합니다.
                // 여기에서는 0으로 처리하도록 했습니다.
                dotProduct += 0;
                normA += 0;
                normB += 0;
            }
        }
    
        // 벡터의 크기가 0인 경우에는 코사인 유사도를 계산할 수 없습니다.
        if (normA == 0 || normB == 0) {
            return 0.0; // 혹은 다른 처리를 원하시면 수정 가능합니다.
        }
    
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static Map<String, Float[]> loadWordVectorsToList(String filePath) throws IOException {
        Map<String, Float[]> wordVectors = new HashMap<>();
        int vectorSize = 100; // 벡터의 크기를 적절하게 설정하세요.
    
        // Word2Vec 모델 파일을 읽어와서 단어와 벡터 매핑을 생성합니다.
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            String[] parts = line.split(" ");
            String word = parts[0];
            Float[] vector = new Float[vectorSize]; // 벡터의 길이를 적절하게 설정하세요.
    
            // 벡터의 길이를 맞추기 위해 zero-padding을 적용합니다.
            for (int i = 1; i < Math.min(parts.length, vectorSize + 1); i++) {
                try {
                    vector[i - 1] = Float.parseFloat(parts[i]); // 각 부분을 Float로 변환합니다.
                } catch (NumberFormatException e) {
                    // 변환할 수 없는 경우에는 0으로 채웁니다.
                    vector[i - 1] = 0.0f;
                }
            }
            // 여기서부터 ***********
            if (vector.length < vectorSize) {
                Float[] paddedVector = new Float[vectorSize];
                System.arraycopy(vector, 0, paddedVector, 0, vector.length);
                wordVectors.put(word, paddedVector);
            } else {
                wordVectors.put(word, vector);
            }
            // 여기까지 ***********
        }
    
        return wordVectors;
    }
}
