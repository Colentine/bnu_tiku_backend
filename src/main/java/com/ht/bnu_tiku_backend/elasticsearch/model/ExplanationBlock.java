package com.ht.bnu_tiku_backend.elasticsearch.model;

import lombok.Data;

@Data
public class ExplanationBlock {
    Explanation explanation;

    Analysis analysis;

    FinishingTouch finishingTouch;
}
