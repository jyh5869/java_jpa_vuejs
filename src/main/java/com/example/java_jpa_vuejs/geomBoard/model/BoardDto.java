package com.example.java_jpa_vuejs.geomBoard.model;

import jakarta.persistence.Id;

import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;

import java.time.ZonedDateTime;

//import com.google.gson.JsonObject;

import org.springframework.data.geo.Circle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class BoardDto {

	@Id
	private long id;

	private String docId;

	private String actionType;

	private String boardSq;
	
	private String geomPolygons;
	
	private String geomLineStrings;

	private String geomPoints;

	private String geomCircles;

	private String geomPolygonCircles;

	private String[] geomDeleteArr;

	private String userEmail;

	private String userNm;

	private String userAdress;

	private String title;

	private String contents1;

	private String contents2;

	private String state;

	private String zipCd;

	private String useYn;

	private String createdDate;

	private String modifiedDate;

}