package com.ht.bnu_tiku_backend.mongodb.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 年级
 * @TableName grade
 */
@Document("grade")
@Data
public class Grade {
    /**
     * 
     */
    private Long id;

    /**
     * 年级名称
     */
    private String name;

    /**
     * 年级描述
     */
    private String description;
}