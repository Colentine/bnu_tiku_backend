package com.ht.bnu_tiku_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.model.domain.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import com.ht.bnu_tiku_backend.utils.RecievedParameters.QuestionRecieved;

import java.util.List;
import java.util.Map;

/**
* @author huangtao
* @description 针对表【question(习题表)】的数据库操作Service
* @createDate 2025-04-17 15:23:47
*/
public interface QuestionService extends IService<Question> {

    /**
     * 根据知识点查题
     * @param knowledgePointIdlist
     * @return
     * @throws JsonProcessingException
     */
    List<Map<String,String>> queryQuestionsByIds(List<Long> knowledgePointIdlist) throws JsonProcessingException;

    /**
     * 根据知识点查题
     * @param name
     * @return
     * @throws JsonProcessingException
     */
    List<Map<String,String>> queryQuestionsByKnowledgePoint(String name) throws JsonProcessingException;

    /**
     * 根据来源查题
     * @param name
     * @return
     * @throws JsonProcessingException
     */
    //TODO
    List<Map<String,String>> queryQuestionsBySource(String name);

    /**
     * 根据年级查题
     * @param name
     * @return
     * @throws JsonProcessingException
     */
    //TODO
    List<Map<String,String>> queryQuestionsByGrade(String name);

    /**
     * 根据难度查题
     * @param name
     * @return
     * @throws JsonProcessingException
     */
    //TODO
    List<Map<String,String>> queryQuestionsByDifficulty(String name);

    /**
     * 向数据库中增加题目
     * @param question
     * @throws JsonProcessingException
     */
    //TODO
    void insertQuestion(QuestionRecieved question);

    /**
     * 修改数据库中的题目
     * @param question
     * @throws JsonProcessingException
     */
    //TODO
    void updateQuestion(QuestionRecieved question);

    /**
     * 根据id删题
     * @param id
     * @throws JsonProcessingException
     */
    //TODO
    void deleteQuestion(Long id);

    PageQueryQuestionResult pageQueryQuestionsByKnowledgePoint(String name, Long pageNumber, Long pageSize) throws JsonProcessingException;
}
