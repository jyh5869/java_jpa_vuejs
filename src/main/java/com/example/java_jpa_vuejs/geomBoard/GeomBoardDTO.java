package com.example.java_jpa_vuejs.geomBoard;

import jakarta.persistence.Id;

import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import com.google.gson.JsonObject;

import org.springframework.data.geo.Circle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GeomBoardDto {

	@Id
	private long id;
	
	private String arrpoint;
	
	private String arrpolygon;
	
	private String arrcircle;

}