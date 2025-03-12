package com.example.java_jpa_vuejs.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Document(indexName = "geometry_board")
@Setting(shards = 1, replicas = 0)
public class GeomBoardDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long boardSq;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String contents1;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String contents2;

    @Field(type = FieldType.Keyword)
    private String userName;

    @Field(type = FieldType.Keyword)
    private String userEmail;

    @Field(type = FieldType.Keyword)
    private String userAddress;

    @Field(type = FieldType.Keyword)
    private String zipCode;
    
    @Field(type = FieldType.Boolean)
    private Boolean state;
    
    @Field(type = FieldType.Boolean)
    private Boolean useYn;

    //@Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    //private LocalDateTime regDt;

    @Field(type = FieldType.Text)
    private String regDt;

    @Field(type = FieldType.Object)
    private Map<String, Object> geometry;
}