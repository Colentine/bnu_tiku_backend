package com.ht.bnu_tiku_backend.elasticsearch.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnswerBlock extends QuestionBlock {
    int answerCount;
}
