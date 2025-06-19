package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;

/**
 * 题干表
 *
 * @TableName question_stem_block
 */
@TableName(value = "question_stem_block")
@Data
public class QuestionStemBlock {
    /**
     * 内容块表主键ID，唯一标识一个内容块
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联题目主表的ID，表示该内容块所属的题目
     */
    private Long questionId;

    /**
     * 内容类型，可选值为文本、图片
     */
    private Integer contentType;

    /**
     * 文本的具体内容
     */
    private String textContent;

    /**
     * 图片id
     */
    private Long imageFileId;

    /**
     * 内容在当前内容块的位置（从上往下）
     */
    private Integer position;

    /**
     * 内容块创建时间，默认为当前时间
     */
    private Date createdAt;

    /**
     * 内容块最后更新时间，自动更新为当前时间
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
        QuestionStemBlock other = (QuestionStemBlock) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
                && (this.getContentType() == null ? other.getContentType() == null : this.getContentType().equals(other.getContentType()))
                && (this.getTextContent() == null ? other.getTextContent() == null : this.getTextContent().equals(other.getTextContent()))
                && (this.getImageFileId() == null ? other.getImageFileId() == null : this.getImageFileId().equals(other.getImageFileId()))
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
        result = prime * result + ((getContentType() == null) ? 0 : getContentType().hashCode());
        result = prime * result + ((getTextContent() == null) ? 0 : getTextContent().hashCode());
        result = prime * result + ((getImageFileId() == null) ? 0 : getImageFileId().hashCode());
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
        sb.append(", contentType=").append(contentType);
        sb.append(", textContent=").append(textContent);
        sb.append(", imageFileId=").append(imageFileId);
        sb.append(", position=").append(position);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}