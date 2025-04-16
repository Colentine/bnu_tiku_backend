package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 习题知识点关联表
 * @TableName question_knowledge
 */
@TableName(value ="question_knowledge")
@Data
public class QuestionKnowledge {
    /**
     * 题目与知识点关系表主键ID，唯一标识一条关系
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联题目主表的ID，表示该关系所属的题目
     */
    private Long questionId;

    /**
     * 关联知识点表的ID，表示该关系所属的知识点
     */
    private Long knowledgePointId;

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
        QuestionKnowledge other = (QuestionKnowledge) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
            && (this.getKnowledgePointId() == null ? other.getKnowledgePointId() == null : this.getKnowledgePointId().equals(other.getKnowledgePointId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getKnowledgePointId() == null) ? 0 : getKnowledgePointId().hashCode());
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
        sb.append(", knowledgePointId=").append(knowledgePointId);
        sb.append("]");
        return sb.toString();
    }
}