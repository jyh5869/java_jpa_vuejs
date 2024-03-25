package com.example.java_jpa_vuejs.tensorFlow.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.tensorFlow.common.hunspell;
import com.example.java_jpa_vuejs.tensorFlow.common.levenUtil;
import com.example.java_jpa_vuejs.tensorFlow.common.word2VecUtil;
import com.example.java_jpa_vuejs.tensorFlow.model.AnalyzeDTO;
import com.example.java_jpa_vuejs.tensorFlow.model.RoadDTO;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import scala.collection.Seq;

import org.apache.poi.ss.usermodel.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class word2VecController {

    static final  int ROW = 10000;
	
    private final int FEATURE = 0;
    private final int RETURN_COUNT = 5;
    private final String MODEL_PATH = "C:/Users/all4land/Desktop/korean_word2vec_model.txt";
    private final String FILE_PATH_KOR = "documents/leaningData/addrkor.txt";
    private final String FILE_PATH_ENG = "documents/leaningData/addrEng.txt";

    /**
    * @method 텍스트를 입력받아 벡터모델에서 코사인 유사도로 측정한 유사 도로명 추출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeyword")
    public Map<String, Object> index1231231dfwe23(@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();

        // 입력 키워드
        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        if(correctionYN.equals("Y")){
            inputWord = hunspell.spellingCorrection(inputWord);//맞춤법 및 문법 교정
        }
        
        //1. 생성되어있는 모델 호출하여 분석 , 2.모델의 리스트를 호출하여 코사인 유사도를 측정하여 분석
        if(analyzeType.equals("model")){

            Word2Vec word2VecModel = WordVectorSerializer.readWord2VecModel(MODEL_PATH);//모델 호출

            Collection<String> mostSimilarWordMany = word2VecModel.wordsNearest(inputWord, RETURN_COUNT);

            System.out.println("주어진 단어 '" + inputWord + "'와 유사한 단어 " + RETURN_COUNT + "개 출럭");
            for (String word : mostSimilarWordMany) {
                System.out.println(word);
            }

            retMap.put("code","SUCESS01");
            retMap.put("resuleMany", mostSimilarWordMany);
        }
        else{
            
            Map<String, Float[]> wordVectors = word2VecUtil.loadWordVectorsToList(MODEL_PATH);//모델의 데이터셋 호출
            Float[] inputVector = wordVectors.get(inputWord);//입력키워드 백터화

            if (inputVector == null) {
                //모델에 해당 단어의 벡터가 없는 경우에 대한 예외 처리
                System.out.println("입력한 단어에 대한 벡터가 모델에 존재하지 않습니다.");
                
                retMap.put("code","ERROR01");
            } 
            else {
                //입력 단어의 벡터가 존재하는 경우에는 가장 유사한 10개의 단어를 찾는다.
                List<String> mostSimilarWordMany = word2VecUtil.findMostSimilarWordMany(inputVector, wordVectors, 5);
                String mostSimilarWordOne = word2VecUtil.findMostSimilarWordOne(inputVector, wordVectors);
    
                System.out.println("입력 단어 분석 - START");
                System.out.println("입력된 단어와 가장 유사한 단어 : " + mostSimilarWordOne);
                System.out.println("입력된 단어와 가장 유사한 " + RETURN_COUNT + "개의 리스트 : " + mostSimilarWordOne);
                for (String word : mostSimilarWordMany) {
                    System.out.println(word);
                }
                System.out.println("입력 단어 분석 - END");

                retMap.put("code", "SUCESS01");
                retMap.put("resuleOne", mostSimilarWordOne);
                retMap.put("resuleMany", mostSimilarWordMany);
            }
        }
        retMap.put("analyzeType", analyzeType);
        retMap.put("correctionYN", correctionYN);

        return retMap;
    }

  
    /**
    * @method 레벤슈타인 기법으로 텍스트간의 거리를 측정해서 유사한 단어 추출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getAnalyzeKeywordLeven")
    public Map<String, Object> getAnalyzeKeywordLeven(@Valid AnalyzeDTO analyzeDTO) throws Exception {
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        SentenceIterator iter = new BasicLineIterator(new File(FILE_PATH_KOR));

        try {
            while (iter.hasNext()) {
                String line = iter.nextSentence();
                addrList.add(line);
            }
        } 
        catch(Exception e) {
            e.printStackTrace();
            iter.finish();
        }

        try {
            String mostSimilarWordOne = levenUtil.findMostSimilarWord(inputWord, addrList);
            List<String> mostSimilarWordMany = levenUtil.findTopSimilarWords(inputWord, addrList, 10);

            System.out.println("입력한 단어: " + inputWord + "와 가장 유사한 단어: " + mostSimilarWordOne);
            System.out.println("입력한 단어: " + inputWord + "와 상위 유사한 단어 10개:");
            for (String word : mostSimilarWordMany) {
                System.out.println(word);
            }

            retMap.put("code", "SUCESS01");
            retMap.put("resuleOne", mostSimilarWordOne);
            retMap.put("resuleMany", mostSimilarWordMany);
        } 
        catch(Exception e) {
            e.printStackTrace();
            retMap.put("code", "ERROR01");
        }
        finally{
            retMap.put("analyzeType", analyzeType);
            retMap.put("correctionYN", correctionYN);
        };

        return retMap;
    }





    /**
    * @method Tensor Flow Test( 텍스트 분석 모델 정상동작 샘플 코드 )
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr")
    public Map<String, Object> index123123123(@Valid AnalyzeDTO analyzeDTO) throws Exception {

        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> addrList = new ArrayList<>();

        String inputWord = analyzeDTO.getInputKeyword();
        String analyzeType = analyzeDTO.getAnalyzeType();
        String correctionYN = analyzeDTO.getCorrectionYN();

        String filePathTxt = "C:/Users/all4land/Desktop/test.txt";
        String filePathCsv = "C:/Users/all4land/Desktop/TN_SPRD_RDNM.csv";
        String charsetName = "EUC-KR"; // 파일의 인코딩에 맞게 수정

        // 엑셀 파일에서 과일 리스트 읽어오기
        //List<RoadDTO> fruitList = csvToRoadToList(filePathCsv);
 
        // 문장 이터레이터 생성
        SentenceIterator iter = new BasicLineIterator(new File(filePathTxt));

        List<String> tokens = new ArrayList<>();
        
        try {
             
            while (iter.hasNext()) {
                String line = iter.nextSentence();
                System.out.println("원본 문장: " + line);
                List<String> morphemes = analyzeKoreanText(line);
                
                System.out.println("형태소 분석 결과: " + StringUtils.join(morphemes, ", "));
                System.out.println();
                tokens.add(StringUtils.join(morphemes, " "));
            }
        } catch(Exception e) {
            e.printStackTrace();
            iter.finish();
        }
        
        CollectionSentenceIterator iterToken = new CollectionSentenceIterator(tokens);


        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        
        //tokenizerFactory.setTokenPreProcessor(null);// 단어를 토큰화할 때 빈도수가 낮은 단어를 필터링하지 않고 그대로 포함시킴
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());// 단어를 토큰화할 때 빈도수가 낮은 단어를 필터링

        // Word2Vec 모델을 훈련시킵니다.
        WordVectors word2VecModel = trainWord2VecModel(iter, iterToken, tokenizerFactory);

        // 훈련된 모델을 사용하여 유사한 단어를 찾습니다.
        Collection<String> mostSimilarWordMany = word2VecModel.wordsNearest("노원로", 10);
        
        System.out.println("주어진 단어 '" + "노원로" + "'와 유사한 단어:");
        for (String word : mostSimilarWordMany) {
            System.out.println(word);
        }
    
        return null;
    }
    
    // 한글 텍스트의 형태소 분석을 수행하는 메서드
    public List<String> analyzeKoreanText(String text) {
        // 한글 텍스트를 형태소로 분석
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        
        // 형태소를 추출하여 리스트로 반환
        List<String> morphemes = new ArrayList<>();
        for (KoreanTokenizer.KoreanToken token : scala.collection.JavaConversions.seqAsJavaList(tokens)) {
            morphemes.add(token.text());
        }
        
        return morphemes;
    }


    // Word2Vec 모델을 훈련시키는 메서드
    @SuppressWarnings("deprecation")
    private static WordVectors trainWord2VecModel(SentenceIterator iter, CollectionSentenceIterator token, TokenizerFactory tokenizerFactory) {
        System.out.println("Load & Vectorize Sentences....");
        
        Word2Vec word2Vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(tokenizerFactory)
                .build();
    
        // 모델 학습
        word2Vec.fit();
    
        // 훈련된 Word2Vec 모델을 저장할 수 있습니다.
        try {
            WordVectorSerializer.writeWordVectors(word2Vec, "C:/Users/all4land/Desktop/korean_word2vec_model.txt");
            System.out.println("모델저장 완료");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return word2Vec;
    }



    /**
	 * List<RoadDTO> 형식의 데이터 생성 및 검증
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static List<RoadDTO> csvToRoadToList(String filePath) throws IOException {
		//cSV 파일을 Object 객체로 List에 적재
        try {
            int cnt = 0;
            
            //데이터 검증
            List<RoadDTO> data = readCsvToBean(filePath);
            Iterator<RoadDTO> it = data.iterator();
            while(it.hasNext()) {
                RoadDTO vo = (RoadDTO)it.next();

                System.out.println("num : "+ vo.getRn());


                cnt++;
                if(cnt == 100){break;};
            }

            return data;
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}


     /**
	 * CSV 파일 데이터를 List<RoadDTO> 형식으로 전환
	 * @param filePath
	 * @throws IOException
	 */
    public static List<RoadDTO> readCsvToBean(String filename){
		
		List<RoadDTO> data = null;
		try {
			//csv 파일 읽기
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename),"EUC-KR"),',','"',0);
			
			//CSV 를 VO에 매핑해주는 매퍼 역할을 할 클래스 객체 생성
			ColumnPositionMappingStrategy<RoadDTO> mapper = new ColumnPositionMappingStrategy<RoadDTO>();
			mapper.setType(RoadDTO.class);   //VO파일을 맵핑하겠다.
			String[] columns = new String[] {"sigCd","rnCd","emdNo","rn","engNm","sidoNm","sggNm","emdSe","emdCd","emdNm","useYn","alwncResn","chghy","aftchInfo","ctpEngNm","sigEngNm","emdEngNm","beginBsis","endBsis","effectDe","ersrDe"}; // 각 컬럼을 정의할 배열
			mapper.setColumnMapping(columns); //각 컬럼명을 매퍼에 설정
			
			//매핑하기!!
			CsvToBean<RoadDTO> csv = new CsvToBean<RoadDTO>();
			data = csv.parse(mapper, reader); //(매핑방법, csv파일)
			
			reader.close();
		}
        catch(Exception e) {
			e.getStackTrace();
		}
		
		return data;
	}

}
