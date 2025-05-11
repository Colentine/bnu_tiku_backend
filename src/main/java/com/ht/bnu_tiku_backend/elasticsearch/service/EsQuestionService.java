package com.ht.bnu_tiku_backend.elasticsearch.service;

import com.ht.bnu_tiku_backend.elasticsearch.model.Question;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;

import java.io.IOException;
import java.util.List;

public interface EsQuestionService {
    void saveQuestion(Question question);

    PageQueryQuestionResult queryQuestionsByKnowledgePointNames(List<String> knowledgePointNames, Long pageNumber, Long pageSize) throws IOException;
    PageQueryQuestionResult queryQuestionsByKeyword(String keyword, Long pageNumber, Long pageSize) throws IOException;
}
