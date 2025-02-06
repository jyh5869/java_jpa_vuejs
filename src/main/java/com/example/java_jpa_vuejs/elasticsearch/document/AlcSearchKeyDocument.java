package com.example.java_jpa_vuejs.elasticsearch.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class AlcSearchKeyDocument {
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String key;

    public AlcSearchKeyDocument(String key) {
        this.key = key;
    }
}
