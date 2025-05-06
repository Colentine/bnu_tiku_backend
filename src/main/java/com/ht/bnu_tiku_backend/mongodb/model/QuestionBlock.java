package com.ht.bnu_tiku_backend.mongodb.model;

import lombok.Data;

import java.util.List;

@Data
public class QuestionBlock {
    String text;

    List<Image> images;
}
