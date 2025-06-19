package com.ht.bnu_tiku_backend.mongodb.service;

import com.ht.bnu_tiku_backend.mongodb.model.Question;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;

import java.io.IOException;
import java.util.List;

public interface MongoQuestionService {
    void saveQuestion(Question question);

    List<Question> getAllQuestions();

    PageQueryQuestionResult queryQuestionsByKnowledgePointNames(List<String> knowledgePointNames, Long pageNumber, Long pageSize) throws IOException;
}
