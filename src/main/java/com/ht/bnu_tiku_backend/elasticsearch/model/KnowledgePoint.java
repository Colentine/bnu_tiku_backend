package com.ht.bnu_tiku_backend.elasticsearch.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "knowledge_point")
@Data
public class KnowledgePoint {
    @Field(type = FieldType.Long)
    Long id;

    @Field(type = FieldType.Text)
    String name;

    @Field(name="parent_id", type = FieldType.Long)
    Long parentId;

    @Field(name="name_initials", type = FieldType.Text)
    String nameInitials;

    @Field(name="description", type = FieldType.Text)
    String description;
}