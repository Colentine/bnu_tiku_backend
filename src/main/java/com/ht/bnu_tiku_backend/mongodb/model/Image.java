package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class Image {
    @Field(type = FieldType.Keyword)
    String url;

    @Field(type = FieldType.Keyword)
    Long position;
}
