package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 核心素养
 *
 * @TableName core_competency
 */
@Document("core_competency")
@Data
public class CoreCompetency {
    /**
     *
     */
    private Long id;

    /**
     * 核心素养名称
     */
    private String competencyName;

    /**
     * 核心素养描述
     */
    private String description;

}