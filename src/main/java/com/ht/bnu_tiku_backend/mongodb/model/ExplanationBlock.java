package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;

@Data
public class ExplanationBlock {
    Explanation explanation;

    Analysis analysis;

    FinishingTouch finishingTouch;
}
