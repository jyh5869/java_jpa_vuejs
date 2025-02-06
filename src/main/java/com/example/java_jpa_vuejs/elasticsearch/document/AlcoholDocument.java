package com.example.java_jpa_vuejs.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteOnlyProperty;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(indexName = "alcohols")
@Setting(replicas = 0)
public class AlcoholDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long, index = false, docValues = false)
    private Long alcoholId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String category;

    @Field(type = FieldType.Object)
    private List<TagDocument> tags = new ArrayList<>();

    @Field(type = FieldType.Object)
    @WriteOnlyProperty
    private List<AlcSearchKeyDocument> searchKeys = new ArrayList<>();
}