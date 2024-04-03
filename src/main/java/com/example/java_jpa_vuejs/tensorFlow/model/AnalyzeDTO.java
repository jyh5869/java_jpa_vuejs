package com.example.java_jpa_vuejs.tensorFlow.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class AnalyzeDTO {

    private String inputKeyword;//입력 키워드

	private String analyzeType;//분석 타입 1.모델, 2.리스트-> 코사인유사도

	private String correctionYN;//맞춤법 교정여부

	private String morphologicalYN = "N";//형태소분석 여부

	private String leaningDataType = "FULL";
}
