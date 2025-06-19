package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;

/**
 * 习题修订日志
 *
 * @TableName question_revision_log
 */
@TableName(value = "question_revision_log")
@Data
public class QuestionRevisionLog {
    /**
     * 修订日志表主键ID，唯一标识一条修订日志
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联题目主表的ID，表示该修订日志所属的题目
     */
    private Long questionId;

    /**
     * 修改的字段，可选值为题干、答案、解析、知识点、综合类型、难度等级、核心素养
     */
    private Integer modifiedField;

    /**
     * 修改前的值
     */
    private String oldValue;

    /**
     * 修改后的值
     */
    private String newValue;

    /**
     * 关联用户表的ID，表示执行本次修改的用户
     */
    private Long modifiedBy;

    /**
     * 修改时间，默认为当前时间
     */
    private Date modifiedAt;

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
        QuestionRevisionLog other = (QuestionRevisionLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
                && (this.getModifiedField() == null ? other.getModifiedField() == null : this.getModifiedField().equals(other.getModifiedField()))
                && (this.getOldValue() == null ? other.getOldValue() == null : this.getOldValue().equals(other.getOldValue()))
                && (this.getNewValue() == null ? other.getNewValue() == null : this.getNewValue().equals(other.getNewValue()))
                && (this.getModifiedBy() == null ? other.getModifiedBy() == null : this.getModifiedBy().equals(other.getModifiedBy()))
                && (this.getModifiedAt() == null ? other.getModifiedAt() == null : this.getModifiedAt().equals(other.getModifiedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getModifiedField() == null) ? 0 : getModifiedField().hashCode());
        result = prime * result + ((getOldValue() == null) ? 0 : getOldValue().hashCode());
        result = prime * result + ((getNewValue() == null) ? 0 : getNewValue().hashCode());
        result = prime * result + ((getModifiedBy() == null) ? 0 : getModifiedBy().hashCode());
        result = prime * result + ((getModifiedAt() == null) ? 0 : getModifiedAt().hashCode());
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
        sb.append(", modifiedField=").append(modifiedField);
        sb.append(", oldValue=").append(oldValue);
        sb.append(", newValue=").append(newValue);
        sb.append(", modifiedBy=").append(modifiedBy);
        sb.append(", modifiedAt=").append(modifiedAt);
        sb.append("]");
        return sb.toString();
    }
}