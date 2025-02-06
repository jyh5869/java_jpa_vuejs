package com.example.java_jpa_vuejs.elasticsearch.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class TagDocument {
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String title;

    public TagDocument(String title) {
        this.title = title;
    }
}