package com.ht.bnu_tiku_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.model.domain.Question;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author huangtao
* @description 针对表【question(习题表)】的数据库操作Service
* @createDate 2025-04-17 15:23:47
*/
public interface QuestionService extends IService<Question> {

    List<Map<String,String>> queryQuestionsByKnowledgePoint(String name) throws JsonProcessingException;
}
