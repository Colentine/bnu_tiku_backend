package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 习题表
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long parentId;

    /**
     * 题目类型：简单题（simple）或复合题（composite）
     */
    private Integer questionType;

    /**
     * 简单题的具体类型，选择、填空等
     */
    private Integer simpleQuestionType;

    /**
     * 学科，默认数学
     */
    private String subject;

    /**
     * 年级，关联 grade 表
     */
    private Long gradeId;

    /**
     * 来源，关联 source 表
     */
    private Long sourceId;

    /**
     * 题目难度系数
     */
    private Double difficulty;

    /**
     * 综合类型，关联 complexity_type 表
     */
    private Long complexityTypeId;

    /**
     * 核心素养，关联 core_competency 表
     */
    private Long coreCompetencyId;

    /**
     * 创建人/修改人，关联 user 表
     */
    private Long createdBy;

    /**
     * 
     */
    private Date createdAt;

    /**
     * 
     */
    private Date updatedAt;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Question other = (Question) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getQuestionType() == null ? other.getQuestionType() == null : this.getQuestionType().equals(other.getQuestionType()))
            && (this.getSimpleQuestionType() == null ? other.getSimpleQuestionType() == null : this.getSimpleQuestionType().equals(other.getSimpleQuestionType()))
            && (this.getSubject() == null ? other.getSubject() == null : this.getSubject().equals(other.getSubject()))
            && (this.getGradeId() == null ? other.getGradeId() == null : this.getGradeId().equals(other.getGradeId()))
            && (this.getSourceId() == null ? other.getSourceId() == null : this.getSourceId().equals(other.getSourceId()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getComplexityTypeId() == null ? other.getComplexityTypeId() == null : this.getComplexityTypeId().equals(other.getComplexityTypeId()))
            && (this.getCoreCompetencyId() == null ? other.getCoreCompetencyId() == null : this.getCoreCompetencyId().equals(other.getCoreCompetencyId()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getQuestionType() == null) ? 0 : getQuestionType().hashCode());
        result = prime * result + ((getSimpleQuestionType() == null) ? 0 : getSimpleQuestionType().hashCode());
        result = prime * result + ((getSubject() == null) ? 0 : getSubject().hashCode());
        result = prime * result + ((getGradeId() == null) ? 0 : getGradeId().hashCode());
        result = prime * result + ((getSourceId() == null) ? 0 : getSourceId().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getComplexityTypeId() == null) ? 0 : getComplexityTypeId().hashCode());
        result = prime * result + ((getCoreCompetencyId() == null) ? 0 : getCoreCompetencyId().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", parentId=").append(parentId);
        sb.append(", questionType=").append(questionType);
        sb.append(", simpleQuestionType=").append(simpleQuestionType);
        sb.append(", subject=").append(subject);
        sb.append(", gradeId=").append(gradeId);
        sb.append(", sourceId=").append(sourceId);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", complexityTypeId=").append(complexityTypeId);
        sb.append(", coreCompetencyId=").append(coreCompetencyId);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}