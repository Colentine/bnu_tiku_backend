package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document(collection = "question")
public class Question {

    Long questionId;

    Integer questionType;

    Integer simpleQuestionType;

    Long parentId;

    Double difficulty;

    List<Long> knowledgePointIds;

    Integer gradeId;

    Long sourceId;

    Long coreCompetencyId;

    Long complexityId;

    Long createdBy;

    StemBlock stemBlock;

    AnswerBlock answerBlock;

    ExplanationBlock explanationBlock;


    @CreatedDate
    private String createdAt;

    @LastModifiedDate
    private String updatedAt;
}
