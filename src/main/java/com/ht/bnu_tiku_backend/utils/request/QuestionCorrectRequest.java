package com.ht.bnu_tiku_backend.utils.request;

import lombok.Data;

@Data
public class QuestionCorrectRequest {

    Integer userId;

    String correctType;

    String correction;

    Integer questionId;

    CorrectTags correctTags;
}

