package com.example.java_jpa_vuejs.elasticsearch.model;

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

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class AlcoholDto {
	@Id
	private String id;
    private Long tableId;
    private String title;
    private String contents;
    private String category;
    //private List<TagDTO> tags;
    //private List<AlcSearchKeyDTO> searchKeys;
}