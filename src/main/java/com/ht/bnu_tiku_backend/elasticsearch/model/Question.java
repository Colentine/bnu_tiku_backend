package com.ht.bnu_tiku_backend.elasticsearch.model;

import com.ht.bnu_tiku_backend.mongodb.model.AnswerBlock;
import com.ht.bnu_tiku_backend.mongodb.model.ExplanationBlock;
import com.ht.bnu_tiku_backend.mongodb.model.StemBlock;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "question")
public class Question {

    @Id
    @Field(name = "question_id", type = FieldType.Long)
    private Long questionId;

    @Field(name = "question_type", type = FieldType.Integer)
    private Integer questionType;

    @Field(name = "simple_question_type", type = FieldType.Integer)
    private Integer simpleQuestionType;

    @Field(name = "parent_id", type = FieldType.Long)
    private Long parentId;

    @Field(name = "difficulty", type = FieldType.Double)
    private Double difficulty;

    @Field(name = "knowledge_point_ids", type = FieldType.Long)
    private List<Long> knowledgePointIds;

    @Field(name = "grade_id", type = FieldType.Integer)
    private Integer gradeId;

    @Field(name = "source_id", type = FieldType.Long)
    private Long sourceId;

    @Field(name = "core_competency_id", type = FieldType.Long)
    private Long coreCompetencyId;

    @Field(name = "complexity_id", type = FieldType.Long)
    private Long complexityId;

    @Field(name = "created_by", type = FieldType.Long)
    private Long createdBy;

    @Field(name = "stem_block", type = FieldType.Object)
    private StemBlock stemBlock;

    @Field(name = "answer_block", type = FieldType.Object)
    private AnswerBlock answerBlock;

    @Field(name = "explanation_block", type = FieldType.Object)
    private ExplanationBlock explanationBlock;

    @CreatedDate
    @Field(name = "created_at", type = FieldType.Date)
    private Date createdAt;

    @LastModifiedDate
    @Field(name = "updated_at", type = FieldType.Date)
    private Date updatedAt;
}
