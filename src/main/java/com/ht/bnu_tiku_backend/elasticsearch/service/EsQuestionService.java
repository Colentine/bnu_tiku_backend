package com.ht.bnu_tiku_backend.elasticsearch.service;

import com.ht.bnu_tiku_backend.elasticsearch.model.Question;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import com.ht.bnu_tiku_backend.utils.request.QuestionCorrectRequest;
import com.ht.bnu_tiku_backend.utils.request.QuestionSearchRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface EsQuestionService {
    void saveQuestion(Question question);

    PageQueryQuestionResult queryQuestionsByKnowledgePointNames(List<String> knowledgePointNames, Long pageNumber, Long pageSize) throws IOException;

    PageQueryQuestionResult queryQuestionsByKeyword(String keyword, Long pageNumber, Long pageSize) throws IOException;

    File generateDocx(List<Long> ids);

    File generatePdf(List<Long> ids);

    PageQueryQuestionResult searchQuestionByCombination(QuestionSearchRequest questionSearchRequest);

    Result<String> questionCorrect(QuestionCorrectRequest questionCorrectRequest);
}
