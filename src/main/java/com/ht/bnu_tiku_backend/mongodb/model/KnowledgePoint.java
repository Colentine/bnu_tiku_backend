package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("knowledge_point")
@Data
public class KnowledgePoint {
    Long id;

    String name;

    Long parentId;

    String nameInitials;

    String description;
}
