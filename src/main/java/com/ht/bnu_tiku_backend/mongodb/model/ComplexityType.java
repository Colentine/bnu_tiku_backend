package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 综合类型
 *
 * @TableName complexity_type
 */
@Document("complexity_type")
@Data
public class ComplexityType {

    private Long id;

    /**
     * 综合类型名称
     */
    private String typeName;

    /**
     * 类型描述
     */
    private String description;
}