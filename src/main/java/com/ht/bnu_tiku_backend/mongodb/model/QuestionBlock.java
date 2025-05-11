package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
public class QuestionBlock {
    @Field(type = FieldType.Text)
    String text;

    @Field(type = FieldType.Nested)
    List<Image> images;
}
