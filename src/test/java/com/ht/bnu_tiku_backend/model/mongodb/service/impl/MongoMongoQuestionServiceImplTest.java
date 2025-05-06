package com.ht.bnu_tiku_backend.model.mongodb.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.mapper.ComplexityTypeMapper;
import com.ht.bnu_tiku_backend.mapper.CoreCompetencyMapper;
import com.ht.bnu_tiku_backend.mapper.GradeMapper;
import com.ht.bnu_tiku_backend.mapper.SourceMapper;
import com.ht.bnu_tiku_backend.model.domain.ComplexityType;
import com.ht.bnu_tiku_backend.model.domain.Grade;
import com.ht.bnu_tiku_backend.model.domain.Source;
import com.ht.bnu_tiku_backend.model.domain.CoreCompetency;
import com.ht.bnu_tiku_backend.mongodb.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

import com.ht.bnu_tiku_backend.mongodb.model.Question;
import com.ht.bnu_tiku_backend.mongodb.service.MongoUserService;
import com.ht.bnu_tiku_backend.mongodb.service.impl.MongoMongoQuestionServiceImpl;
import com.ht.bnu_tiku_backend.service.QuestionService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoMongoQuestionServiceImplTest {
    @Resource
    private MongoMongoQuestionServiceImpl mongoQuestionService;

    @Resource
    private QuestionService questionService;
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private SourceMapper sourceMapper;
    @Autowired
    private CoreCompetencyMapper coreCompetencyMapper;
    @Autowired
    private ComplexityTypeMapper complexityTypeMapper;
    @Autowired
    private MongoUserService mongoUserService;


    @Test
    public void saveQuestionTest() {
        Question question = new Question();
        question.setQuestionId(2L);
        question.setQuestionType(0);
        question.setSimpleQuestionType(0);
        question.setParentId(0L);
        question.setDifficulty(0.0D);
        question.setKnowledgePointIds(Lists.newArrayList());
        question.setGradeId(0);
        question.setSourceId(0L);
        question.setCoreCompetencyId(0L);
        question.setComplexityId(0L);
        question.setCreatedBy(0L);

        StemBlock stemBlock = new StemBlock();
        stemBlock.setText("123");
        Image image = new Image();
        image.setUrl("");
        image.setPosition(0L);
        stemBlock.setImages(Collections.singletonList(image));
        question.setStemBlock(stemBlock);

        AnswerBlock answerBlock = new AnswerBlock();
        answerBlock.setText("123");
        Image image2 = new Image();
        image2.setUrl("");
        image2.setPosition(0L);
        question.setAnswerBlock(answerBlock);

        ExplanationBlock explanationBlock = new ExplanationBlock();
        Explanation explanation = new Explanation();
        explanation.setText("explanation!!!");
        explanationBlock.setExplanation(explanation);
        question.setExplanationBlock(explanationBlock);

        mongoQuestionService.saveQuestion(question);
    }

    @Test
    public void getAllQuestionsTest() {
        List<Question> allQuestions = mongoQuestionService.getAllQuestions();
        for (Question question : allQuestions) {
            System.out.println(question.getExplanationBlock().getExplanation().getText());
        }

    }

    @Test
    public void InsertMysqlQuestionIntoMongoTest() throws JsonProcessingException {
        questionService.queryQuestionsByKnowledgePoint("beforeMount").stream().forEach(mysqlQuestion -> {
            Question question = new Question();
            ObjectMapper objectMapper = new ObjectMapper();
            QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
            QueryWrapper<Source> sourceQueryWrapper = new QueryWrapper<>();
            QueryWrapper<CoreCompetency> coreCompetencyQueryWrapper = new QueryWrapper<>();
            QueryWrapper<ComplexityType> complexityQueryWrapper = new QueryWrapper<>();
            question.setQuestionId(Long.valueOf(mysqlQuestion.get("question_id")));
            question.setQuestionType(Integer.valueOf(mysqlQuestion.get("question_type")));
            question.setSimpleQuestionType(Integer.valueOf(mysqlQuestion.get("simple_question_type")));
            question.setDifficulty(Double.valueOf(mysqlQuestion.get("difficulty")));
            //System.out.println(mysqlQuestion.get("knowledge_point_list"));
            try {
                question.setKnowledgePointIds(objectMapper.readValue(
                        mysqlQuestion.get("knowledge_point_list")
                        , new TypeReference<List<Long>>() {}
                ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            gradeQueryWrapper.eq("name", mysqlQuestion.get("grade"));
            question.setGradeId(Math.toIntExact(gradeMapper.selectOne(gradeQueryWrapper).getId()));

            sourceQueryWrapper.eq("name", mysqlQuestion.get("question_source"));
            question.setSourceId(sourceMapper.selectOne(sourceQueryWrapper).getId());

            coreCompetencyQueryWrapper.eq("competency_name", mysqlQuestion.get("core_competency"));
            question.setCoreCompetencyId(coreCompetencyMapper.selectOne(coreCompetencyQueryWrapper).getId());

            complexityQueryWrapper.eq("type_name", mysqlQuestion.get("complexity_type"));
            question.setComplexityId(complexityTypeMapper.selectOne(complexityQueryWrapper).getId());

            question.setCreatedBy(1L);
            if(mysqlQuestion.get("question_type").equals("0")){
                StemBlock stemBlock = new StemBlock();
                stemBlock.setText(mysqlQuestion.get("stem"));
                question.setStemBlock(stemBlock);
                AnswerBlock answerBlock = new AnswerBlock();
                answerBlock.setText(mysqlQuestion.get("question_answer"));
                answerBlock.setAnswerCount(answerBlock.getAnswerCount() + 1);
                question.setAnswerBlock(answerBlock);
                ExplanationBlock explanationBlock = new ExplanationBlock();
                Explanation explanation = new Explanation();
                explanation.setText(mysqlQuestion.get("question_explanation"));
                explanationBlock.setExplanation(explanation);
                question.setExplanationBlock(explanationBlock);
                mongoQuestionService.saveQuestion(question);
            }else{
                StemBlock stemBlock = new StemBlock();
                stemBlock.setText(mysqlQuestion.get("composite_question_stem"));
                question.setStemBlock(stemBlock);
                List<Map<String, String>> subQuestions = new ArrayList<>();
                String subQuestionString = mysqlQuestion.get("sub_questions");
                //System.out.println(subQuestionString);
                if(StringUtils.isNotBlank(subQuestionString)){
                    try {
                        subQuestions.addAll(objectMapper.readValue(
                                subQuestionString,
                                new TypeReference<>() {
                                }));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(subQuestions);
                if(!subQuestions.isEmpty()){
                    subQuestions.forEach(subQuestionMap -> {
                        Question subQuestion = new Question();
                        subQuestion.setQuestionId(Long.valueOf(subQuestionMap.get("question_id")));
                        subQuestion.setParentId(question.getQuestionId());
                        subQuestion.setSimpleQuestionType(Integer.valueOf(subQuestionMap.get("simple_question_type")));
                        StemBlock subQuestionStemBlock = new StemBlock();
                        subQuestionStemBlock.setText(subQuestionMap.get("stem"));
                        subQuestion.setStemBlock(subQuestionStemBlock);
                        AnswerBlock subQuestionAnswerBlock = new AnswerBlock();
                        subQuestionAnswerBlock.setText(subQuestionMap.get("question_answer"));
                        subQuestion.setAnswerBlock(subQuestionAnswerBlock);
                        ExplanationBlock subQuestionExplanationBlock = new ExplanationBlock();
                        Explanation explanation = new Explanation();
                        explanation.setText(subQuestionMap.get("question_explanation"));
                        subQuestionExplanationBlock.setExplanation(explanation);
                        subQuestion.setExplanationBlock(subQuestionExplanationBlock);
                        mongoQuestionService.saveQuestion(subQuestion);
                    });
                }
                mongoQuestionService.saveQuestion(question);
            }
        });
    }

    @Test
    public void queryQuestionsByKnowledgePointNames() throws IOException {
        mongoQuestionService.queryQuestionsByKnowledgePointNames(
                List.of("beforeMount"),
                1L,
                5L);

    }
}