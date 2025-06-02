package com.ht.bnu_tiku_backend.utils.request;

import lombok.Data;

@Data
public class QuestionSearchRequest {
    Long knowledgePointId;

    String keyword;

    String difficulty;

    String grade;

    String questionType;

    Long pageNumber;

    Long pageSize;
}