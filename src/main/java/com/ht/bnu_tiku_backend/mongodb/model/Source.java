package com.ht.bnu_tiku_backend.mongodb.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 习题来源表
 *
 * @TableName source
 */
@Document("source")
@Data
public class Source {
    private Long id;

    /**
     * 题目来源名称
     */
    private String name;

    /**
     * 来源描述
     */
    private String description;
}