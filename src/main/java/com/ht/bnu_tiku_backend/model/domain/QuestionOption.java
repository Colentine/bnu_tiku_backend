package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 选择题选项表
 * @TableName question_option
 */
@TableName(value ="question_option")
@Data
public class QuestionOption {
    /**
     * 选项表主键ID，唯一标识一个选项
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联题目主表的ID，表示该选项所属的题目
     */
    private Long questionId;

    /**
     * 选项标签，如A、B、C、D
     */
    private String label;

    /**
     * 选项的具体内容
     */
    private String content;

    /**
     * 关联图像表的ID，表示该选项所含的图像
     */
    private Object imageFileIds;

    /**
     * 图像在选项中的位置
     */
    private Object imagePositions;

    /**
     * 是否为正确答案，默认为FALSE
     */
    private Integer isCorrect;

    /**
     * 选项创建时间，默认为当前时间
     */
    private Date createdAt;

    /**
     * 选项最后更新时间，自动更新为当前时间
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
        QuestionOption other = (QuestionOption) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
            && (this.getLabel() == null ? other.getLabel() == null : this.getLabel().equals(other.getLabel()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getImageFileIds() == null ? other.getImageFileIds() == null : this.getImageFileIds().equals(other.getImageFileIds()))
            && (this.getImagePositions() == null ? other.getImagePositions() == null : this.getImagePositions().equals(other.getImagePositions()))
            && (this.getIsCorrect() == null ? other.getIsCorrect() == null : this.getIsCorrect().equals(other.getIsCorrect()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getImageFileIds() == null) ? 0 : getImageFileIds().hashCode());
        result = prime * result + ((getImagePositions() == null) ? 0 : getImagePositions().hashCode());
        result = prime * result + ((getIsCorrect() == null) ? 0 : getIsCorrect().hashCode());
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
        sb.append(", label=").append(label);
        sb.append(", content=").append(content);
        sb.append(", imageFileIds=").append(imageFileIds);
        sb.append(", imagePositions=").append(imagePositions);
        sb.append(", isCorrect=").append(isCorrect);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}