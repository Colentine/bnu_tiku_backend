package com.ht.bnu_tiku_backend.elasticsearch.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsQuestionRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsQuestionService;
import com.ht.bnu_tiku_backend.mapper.ComplexityTypeMapper;
import com.ht.bnu_tiku_backend.mapper.CoreCompetencyMapper;
import com.ht.bnu_tiku_backend.mapper.GradeMapper;
import com.ht.bnu_tiku_backend.mapper.SourceMapper;
import com.ht.bnu_tiku_backend.model.domain.ComplexityType;
import com.ht.bnu_tiku_backend.model.domain.CoreCompetency;
import com.ht.bnu_tiku_backend.model.domain.Grade;
import com.ht.bnu_tiku_backend.model.domain.Source;
import com.ht.bnu_tiku_backend.mongodb.model.Explanation;
import com.ht.bnu_tiku_backend.mongodb.model.ExplanationBlock;
import com.google.common.collect.Lists;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ht.bnu_tiku_backend.mongodb.model.StemBlock;
import com.ht.bnu_tiku_backend.mongodb.model.AnswerBlock;

import com.ht.bnu_tiku_backend.elasticsearch.model.Question;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.utils.WordUtils;
import com.ht.bnu_tiku_backend.utils.request.QuestionSearchRequest;
import com.latextoword.Latex_Word;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsQuestionServiceImplTest {
    @Resource
    private EsQuestionServiceImpl esQuestionServiceImpl;

    @Resource
    private EsQuestionRepository esQuestionRepository;

    @Resource
    private QuestionService questionService;

    @Resource
    private GradeMapper gradeMapper;

    @Resource
    private SourceMapper sourceMapper;

    @Resource
    private CoreCompetencyMapper coreCompetencyMapper;

    @Resource
    private ComplexityTypeMapper complexityTypeMapper;
    @Autowired
    private EsQuestionServiceImpl esQuestionService;

    @Test
    public void insertSampleQuestionTest() {
        Question question = new Question();
        question.setQuestionId(0L);
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
        question.setStemBlock(new StemBlock());
        question.setAnswerBlock(new AnswerBlock());
        question.setExplanationBlock(new ExplanationBlock());

        esQuestionServiceImpl.saveQuestion(question);
    }

    @Test
    public void InsertMysqlQuestionIntoEsTest() throws JsonProcessingException {
        questionService.queryQuestionsByKnowledgePoint("beforeMount").forEach(mysqlQuestion -> {
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
            } catch (IOException e) {
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
                esQuestionServiceImpl.saveQuestion(question);
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
                    } catch (IOException e) {
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
                        esQuestionServiceImpl.saveQuestion(subQuestion);
                    });
                }
                esQuestionServiceImpl.saveQuestion(question);
            }
        });
    }

    @Test
    public void saveQuestion() {
        System.out.println("yes");
    }

    @Test
    public void queryQuestionsByKnowledgePointNames() throws IOException {
        System.out.println(esQuestionServiceImpl.queryQuestionsByKnowledgePointNames(List.of("beforeMount"), 1297L, 10L));
    }

    @Test
    public void queryQuestionsByKeyword() throws IOException {
        System.out.println(esQuestionServiceImpl.queryQuestionsByKeyword("数", 1297L, 10L));
    }

    @Test
    public void generateDocx() {
        esQuestionService.generateDocx(List.of(1L,2L));
    }

    @Test
    public void generatePdf() {
    }

    @Test
    public void latexToMathml(){
        String latexStr="$$2+x$$";
        String omml= Latex_Word.latexToWord(latexStr);
        System.out.println(omml);
    }

    @Test
    public void splitContent() {
        List<Object> objects = esQuestionService.splitContent("解析：<br/>(1) 解：点$$A$$，$$B$$，$$C$$即为如图所示．\n" +
                "<img alt=\"\" height=\"51\" src=\"https://tiku-pro-cdn.speiyou.com/wxtiku/imgFile/75db5cde9ba63ba28454fac8a784db7c.png\" width=\"493\"/>\n" +
                "(2) 解：$$5-\\left( -2.5 \\right)=7.5$$(千米)．\n" +
                "故超市和姥爷家相距$$7.5$$千米．\n" +
                "(3) 解：$$\\left( 5+2.5+10+2.5 \\right)\\times 0.08=1.6$$(升)．\n" +
                "故小轿车的耗油量是$$1.6$$升．<br/>");
        System.out.println(objects);
    }

    @Test
    public void appendOmmlToParagraph() {
    }

    @Test
    public void insertTagsIntoResult() {
    }

    @Test
    public void insertBlockTextIntoResult() {
    }

    @Test
    public void insertImageUrlIntoText() {
    }

    @Test
    public void searchQuestionByCombination() {
        QuestionSearchRequest questionSearchRequest = new QuestionSearchRequest();
        questionSearchRequest.setKnowledgePointName("");
        questionSearchRequest.setKeyword("");
        questionSearchRequest.setDifficulty("difficult");
        questionSearchRequest.setGradeId(-1);
        questionSearchRequest.setSimpleQuestionType(-1);
        questionSearchRequest.setPageNumber(0L);
        questionSearchRequest.setPageSize(10L);

        esQuestionService.searchQuestionByCombination(questionSearchRequest);

    }

    @Test
    public void testGenerateDocx() {

    }

    @Test
    public void testGeneratePdf() {
        System.out.println(esQuestionService.generateDocx(List.of(81L, 79L, 82L)));
//        System.out.println(esQuestionService.generateDocx(List.of(5001L, 5000L, 5000L)));
    }
}