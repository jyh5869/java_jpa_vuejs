package com.example.java_jpa_vuejs.tensorFlow.common;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.el.ELException;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.word2vec.VocabWord;

import scala.collection.Seq;


@SuppressWarnings("deprecation")
public class word2VecUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(word2VecUtil.class);

    private final static String MODEL_PATH_WORD2VEC_BIN_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_bin_full.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_FULL = "C:/Users/all4land/Desktop/adress_word2Vec_vec_full.vec";

    private final static String MODEL_PATH_WORD2VEC_BIN_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_bin_road.bin";
    private final static String MODEL_PATH_WORD2VEC_VEC_ROAD = "C:/Users/all4land/Desktop/adress_word2Vec_vec_road.vec";


    /* 단어간의 유사도를 계산하여 분석데이터를 만드는 함수(Many) */
    public static List<String> findMostSimilarWordMany(Float[] inputVector, Map<String, Float[]> wordVectors, int numSimilarWords) {
        
        Map<String, Double> similarityMap = new HashMap<>();

        //입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산
        for (Map.Entry<String, Float[]> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            Float[] vector = entry.getValue();

            //유사도 계산
            double similarity = cosineSimilarity(inputVector, vector);

            similarityMap.put(word, similarity);
        }

        //유사도를 기준으로 내림차순으로 정렬
        List<Entry<String, Double>> sortedSimilarityList = new ArrayList<>(similarityMap.entrySet());
        sortedSimilarityList.sort(Entry.comparingByValue(Comparator.reverseOrder()));

        //상위 단어를 선택
        List<String> mostSimilarWords = new ArrayList<>();
        for (int i = 0; i < Math.min(numSimilarWords, sortedSimilarityList.size()); i++) {
            mostSimilarWords.add(sortedSimilarityList.get(i).getKey());
        }

        return mostSimilarWords;
    }


    /* 단어간의 유사도를 계산하여 분석데이터를 만드는 함수(One) */
    public static String findMostSimilarWordOne(Float[] inputVector, Map<String, Float[]> wordVectors) {
        
        if (inputVector == null) {
            return "해당 단어에 대한 백터 값이 없습니다.";//입력된 단어가 모델에 있는 경우 실행 중지
        }
        
        double maxSimilarity = Double.MIN_VALUE;
        String mostSimilarWord = null;
    
        //입력 단어와 모든 학습된 단어들 간의 코사인 유사도를 계산
        for (Map.Entry<String, Float[]> entry : wordVectors.entrySet()) {
            String word = entry.getKey();
            Float[] vector = entry.getValue();
    
            //입력값과 모델에 있는 단어들 간의 유사도를 계산
            double similarity = cosineSimilarity(inputVector, vector);
    
            //가장 유사도가 높은 단어를 업데이트
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarWord = word;
            }
        }

        return mostSimilarWord;
    }


    /* 실제 코사인 유사도 계산 함수 */
    public static double cosineSimilarity(Float[] vectorA, Float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
    
        //두 벡터의 길이가 같은지 확인
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vector lengths must be equal");
        }
    
        for (int i = 0; i < vectorA.length; i++) {
            if (vectorA[i] != null && vectorB[i] != null) {
                dotProduct += vectorA[i] * vectorB[i];
                normA += Math.pow(vectorA[i], 2);
                normB += Math.pow(vectorB[i], 2);
            } else {
                //두 벡터의 해당 원소 중 하나가 null인 경우를 처리(0으로)
                dotProduct += 0;
                normA += 0;
                normB += 0;
            }
        }
    
        //벡터의 크기가 0인 경우에는 코사인 유사도를 계산할 수 없다.
        if (normA == 0 || normB == 0) {
            return 0.0; //혹은 다른 처리를 원하시면 수정 가능
        }
    
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }


    /* 모델에있는 백터데이터를 리스트로 호출 */
    public static Map<String, Float[]> loadWordVectorsToList(String filePath) throws IOException {
        Map<String, Float[]> wordVectors = new HashMap<>();
        int vectorSize = 100;// 벡터의 크기를 적절하게 설정
    
        //Word2Vec 모델 파일을 읽어와서 단어와 벡터 매핑을 생성
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            String[] parts = line.split(" ");
            String word = parts[0];
            Float[] vector = new Float[vectorSize]; //벡터의 길이를 적절하게 설정
    
            //벡터의 길이를 맞추기 위해 zero-padding을 적용
            for (int i = 1; i < Math.min(parts.length, vectorSize + 1); i++) {
                try {
                    vector[i - 1] = Float.parseFloat(parts[i]); // 각 부분을 Float로 변환
                } catch (NumberFormatException e) {
                    //변환할 수 없는 경우에는 0으로 세팅
                    vector[i - 1] = 0.0f;
                }
            }

            if (vector.length < vectorSize) {
                Float[] paddedVector = new Float[vectorSize];
                System.arraycopy(vector, 0, paddedVector, 0, vector.length);
                wordVectors.put(word, paddedVector);
            } else {
                wordVectors.put(word, vector);
            }
        }
    
        return wordVectors;
    }


    /* 분석데이터 정제 */
    public static List<String> dataMiningFromResult(List<String> similarWords, String refinementType) {
        LOG.info("데이터 마이닝 타입 : " + refinementType);
        if(refinementType.equals("Road")){
            List<String> resultToList = new ArrayList<>();//형태소 분석이 필요할 경우 형태소 분석후 데이터를 저장

            String[] endings = {"로", "번길", "대로", "길"};

            for (String word : similarWords) {
                for (String ending : endings) {
                    if (word.endsWith(ending)) {
                        resultToList.add(word);
                        break;
                    }
                }
            }

            return resultToList;
        }
        else{
            return similarWords;
        }
    }


    /* 백터를 값으로 받아 코사인 유사도 계산 */
    public static double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += Math.pow(vec1.get(i), 2);
            norm2 += Math.pow(vec2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }


    /* 한글 텍스트의 형태소 분석을 수행하는 함수*/
    public static List<String> analyzeKoreanText(String text) {
        //한글 텍스트를 형태소로 분석
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        
        //형태소를 추출하여 리스트로 반환
        List<String> morphemes = new ArrayList<>();
        for (KoreanTokenizer.KoreanToken token : scala.collection.JavaConversions.seqAsJavaList(tokens)) {
            morphemes.add(token.text());
        }
        
        return morphemes;
    }


    /* Word2Vec 모델을 훈련시키는 함수*/
    @SuppressWarnings("unused" )
    public static WordVectors trainWord2VecModel(SentenceIterator iter, CollectionSentenceIterator token, TokenizerFactory tokenizerFactory, InMemoryLookupCache cache, String leaningDataType) {
        System.out.println("Load & Vectorize Sentences....");
        
        Word2Vec word2Vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(tokenizerFactory)
                .vocabCache(cache)
                .elementsLearningAlgorithm(new SkipGram<VocabWord>())
                .build();
        //SkipGram 비지도학습 백터 숫자를기반으로 예측 <--> Supervised지도학습  값과 정답을 매칭시켜 훈련 
    
        //모델 학습
        word2Vec.fit();
    
        //훈련된 Word2Vec 모델을 호출 케이스에 따라 저장.
        try {
            if(leaningDataType.equals("FULL")){
                System.out.println("Make Full Adress Model");
                WordVectorSerializer.writeWord2VecModel(word2Vec, new File(MODEL_PATH_WORD2VEC_BIN_FULL));
                WordVectorSerializer.writeWord2VecModel(word2Vec, new File(MODEL_PATH_WORD2VEC_VEC_FULL));
            }
            else if(leaningDataType.equals("ROAD")){
                System.out.println("Make ROAD Adress Model");
                WordVectorSerializer.writeWord2VecModel(word2Vec, new File(MODEL_PATH_WORD2VEC_BIN_ROAD));
                WordVectorSerializer.writeWord2VecModel(word2Vec, new File(MODEL_PATH_WORD2VEC_VEC_ROAD));
            }

            System.out.println("모델저장 완료");
        } 
        catch (ELException e) {
            e.printStackTrace();
        }
        
        return word2Vec;
    }

    
}
