package com.ht.bnu_tiku_backend.elasticsearch.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 核心素养
 *
 * @IndexName core_competency
 */
@Data
@Document(indexName = "core_competency")
public class CoreCompetency {
    /**
     *
     */
    @Field(type = FieldType.Long)
    private Long id;

    /**
     * 核心素养名称
     */
    @Field(name = "competency_name", type = FieldType.Text)
    private String competencyName;

    /**
     * 核心素养描述
     */
    @Field(name = "description", type = FieldType.Text)
    private String description;

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
        CoreCompetency other = (CoreCompetency) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCompetencyName() == null ? other.getCompetencyName() == null : this.getCompetencyName().equals(other.getCompetencyName()))
                && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCompetencyName() == null) ? 0 : getCompetencyName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", competencyName=").append(competencyName);
        sb.append(", description=").append(description);
        sb.append("]");
        return sb.toString();
    }
}