package com.ht.bnu_tiku_backend.utils.request;

import lombok.Data;

@Data
public class QuestionSearchRequest {

    String knowledgePointName;

    String keyword;

    String difficulty;

    Integer gradeId;

    Integer simpleQuestionType;

    Long pageNumber;

    Long pageSize;
}