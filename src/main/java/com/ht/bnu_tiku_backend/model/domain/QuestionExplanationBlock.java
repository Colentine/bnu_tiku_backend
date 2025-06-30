package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 习题解析块
 *
 * @TableName question_explanation_block
 */
@TableName(value = "question_explanation_block")
@Data
public class QuestionExplanationBlock {
    /**
     * 解析块表主键ID，唯一标识一个题目解析
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联题目块表的ID，表示该答案所属的题目
     */
    private Long questionId;

    /**
     * 解析类型，可选值为分析、详解、点睛
     */
    private Integer explanationType;

    /**
     * 内容类型，可选值为文本、图片
     */
    private Integer contentType;

    /**
     * 图片id
     */
    private Long imageFileId;

    /**
     * 分析、详解或点睛的文本
     */
    private String explanationText;

    /**
     * 第几个作答交互的解析,从1开始
     */
    private Long interactiveIndex;

    /**
     * 内容在当前内容块的位置（从上往下）
     */
    private Integer position;

    /**
     * 答案创建时间，默认为当前时间
     */
    private Date createdAt;

    /**
     * 答案最后更新时间，自动更新为当前时间
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
        QuestionExplanationBlock other = (QuestionExplanationBlock) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
                && (this.getExplanationType() == null ? other.getExplanationType() == null : this.getExplanationType().equals(other.getExplanationType()))
                && (this.getContentType() == null ? other.getContentType() == null : this.getContentType().equals(other.getContentType()))
                && (this.getImageFileId() == null ? other.getImageFileId() == null : this.getImageFileId().equals(other.getImageFileId()))
                && (this.getExplanationText() == null ? other.getExplanationText() == null : this.getExplanationText().equals(other.getExplanationText()))
                && (this.getInteractiveIndex() == null ? other.getInteractiveIndex() == null : this.getInteractiveIndex().equals(other.getInteractiveIndex()))
                && (this.getPosition() == null ? other.getPosition() == null : this.getPosition().equals(other.getPosition()))
                && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getExplanationType() == null) ? 0 : getExplanationType().hashCode());
        result = prime * result + ((getContentType() == null) ? 0 : getContentType().hashCode());
        result = prime * result + ((getImageFileId() == null) ? 0 : getImageFileId().hashCode());
        result = prime * result + ((getExplanationText() == null) ? 0 : getExplanationText().hashCode());
        result = prime * result + ((getInteractiveIndex() == null) ? 0 : getInteractiveIndex().hashCode());
        result = prime * result + ((getPosition() == null) ? 0 : getPosition().hashCode());
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
        sb.append(", questionId=").append(questionId);
        sb.append(", explanationType=").append(explanationType);
        sb.append(", contentType=").append(contentType);
        sb.append(", imageFileId=").append(imageFileId);
        sb.append(", explanationText=").append(explanationText);
        sb.append(", interactiveIndex=").append(interactiveIndex);
        sb.append(", position=").append(position);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}