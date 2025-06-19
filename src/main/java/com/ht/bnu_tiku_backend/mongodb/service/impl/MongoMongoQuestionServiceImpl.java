package com.ht.bnu_tiku_backend.mongodb.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.mapper.ComplexityTypeMapper;
import com.ht.bnu_tiku_backend.mapper.CoreCompetencyMapper;
import com.ht.bnu_tiku_backend.mapper.GradeMapper;
import com.ht.bnu_tiku_backend.mapper.SourceMapper;
import com.ht.bnu_tiku_backend.model.domain.ComplexityType;
import com.ht.bnu_tiku_backend.model.domain.CoreCompetency;
import com.ht.bnu_tiku_backend.model.domain.Grade;
import com.ht.bnu_tiku_backend.model.domain.Source;
import com.ht.bnu_tiku_backend.mongodb.model.*;
import com.ht.bnu_tiku_backend.mongodb.repository.QuestionRepository;
import com.ht.bnu_tiku_backend.mongodb.service.MongoQuestionService;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoMongoQuestionServiceImpl implements MongoQuestionService {
    private static final Map<String, String> nameToId;
    private static final Map<String, String> idToName;
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "KnowledgeTree/xkb_node_to_id.json";
        try (
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)
        ) {
            if (is == null) {
                throw new RuntimeException("字典文件找不到：" + path + "，请确认已放到resources目录下");
            }
            nameToId = objectMapper.readValue(is, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        idToName = nameToId.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private QuestionRepository questionRepository;
    @Resource
    private CoreCompetencyMapper coreCompetencyMapper;
    @Resource
    private ComplexityTypeMapper complexityTypeMapper;
    @Resource
    private SourceMapper sourceMapper;
    @Resource
    private GradeMapper gradeMapper;

    @Override
    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public PageQueryQuestionResult queryQuestionsByKnowledgePointNames(List<String> knowledgePointNames, Long pageNumber, Long pageSize) {
        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();

        if (knowledgePointNames.isEmpty()) {
            return new PageQueryQuestionResult();
        }
        Pageable pageable = PageRequest.of(Math.toIntExact(pageNumber - 1), Math.toIntExact(pageSize));
        List<Question> allQuestions = new ArrayList<>();
        if (knowledgePointNames.contains("beforeMount")) {
            Page<Question> allPages = questionRepository.findAll(pageable);
            allQuestions.addAll(allPages.getContent());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalElements());
        } else {
            List<Long> knowledgePointIdlist = knowledgePointNames.stream().map(key -> {
                String knowledgePointId = nameToId.get(key);
                if (StringUtils.isEmpty(knowledgePointId)) {
                    return -1L;
                }
                return Long.valueOf(knowledgePointId);
            }).toList();
//            System.out.println(knowledgePointIdlist);
            //List<Question> byKnowledgePointIdsIn = questionRepository.findByKnowledgePointIdsIn(knowledgePointIdlist);
            //System.out.println(byKnowledgePointIdsIn);
            Page<Question> allPages = questionRepository.findByKnowledgePointIdsIn(knowledgePointIdlist, pageable);
            allQuestions.addAll(allPages.getContent());
//            System.out.println(allPages.getContent());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalElements());
            if (allQuestions.isEmpty()) {
                return new PageQueryQuestionResult();
            }
        }
        List<Long> coreCompetencyIds = allQuestions.stream().map(Question::getCoreCompetencyId).toList();
        Map<Long, CoreCompetency> coreCompetencyMap = coreCompetencyMapper.selectBatchIds(coreCompetencyIds)
                .stream()
                .collect(Collectors.toMap(CoreCompetency::getId, c -> c));

        List<Long> complexityTypeIds = allQuestions.stream().map(Question::getComplexityId).toList();
        Map<Long, ComplexityType> complexityTypeMap = complexityTypeMapper.selectBatchIds(complexityTypeIds)
                .stream()
                .collect(Collectors.toMap(ComplexityType::getId, c -> c));

        List<Long> sourceIds = allQuestions.stream().map(Question::getSourceId).toList();
        Map<Long, Source> sourceMap = sourceMapper.selectBatchIds(sourceIds)
                .stream()
                .collect(Collectors.toMap(Source::getId, c -> c));
//        System.out.println(sourceMap);
//        System.out.println(sourceIds);
        List<Integer> gradeIds = allQuestions.stream().map(Question::getGradeId).toList();
        Map<Long, Grade> gradeMap = gradeMapper.selectBatchIds(gradeIds)
                .stream()
                .collect(Collectors.toMap(Grade::getId, g -> g));

        Map<Long, List<Long>> knowledgePointIdMap = allQuestions
                .stream()
                .collect(Collectors.toMap(Question::getQuestionId, question -> {
                    if (question.getParentId() == null) {
                        return question.getKnowledgePointIds();
                    }
                    return List.of();
                }));
        Map<Long, Long> maxKnowledgePointIdMap = knowledgePointIdMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    if (e.getValue() == null || e.getValue().isEmpty()) {
                        return 0L;
                    }
                    return Collections.max(e.getValue());
                }));

        List<Map<String, String>> queryResult = new ArrayList<>();
        allQuestions.forEach(question -> {
            if (question.getParentId() != null) {
                return;
            }
            HashMap<String, String> questionMap = new HashMap<>();
            StringBuilder stemText = insertTagsIntoResult(question,
                    false,
                    questionMap,
                    complexityTypeMap,
                    coreCompetencyMap,
                    gradeMap,
                    sourceMap,
                    maxKnowledgePointIdMap);
            if (question.getQuestionType().equals(0)) {
                questionMap.put("stem", stemText.toString());
                insertBlockTextIntoResult(question, questionMap);
            } else {
                questionMap.put("composite_question_stem", stemText.toString());
                ArrayList<HashMap<String, String>> subQuestionMaps = new ArrayList<>();
                questionRepository.findByParentId(question.getQuestionId()).forEach(subQuestion -> {
                    HashMap<String, String> subQuestionMap = new HashMap<>();
                    StemBlock subQuestionStemBlock = subQuestion.getStemBlock();
                    StringBuilder subQuestionStemText = new StringBuilder(subQuestionStemBlock.getText());
                    insertImageUrlIntoText(subQuestionStemBlock, subQuestionStemText);
                    subQuestionMap.put("stem", subQuestionStemText.toString());
                    insertTagsIntoResult(subQuestion,
                            true, subQuestionMap,
                            null,
                            null,
                            null,
                            null,
                            null);
                    insertBlockTextIntoResult(subQuestion, subQuestionMap);
                    subQuestionMaps.add(subQuestionMap);
                });
                StringBuilder subQuestionString = new StringBuilder();
                try {
                    subQuestionString.append(objectMapper.writeValueAsString(subQuestionMaps));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                questionMap.put("sub_questions", subQuestionString.toString());
            }
            queryResult.add(questionMap);
        });
        pageQueryQuestionResult.setPageSize(pageSize);
        pageQueryQuestionResult.setPageNo(pageNumber);
        pageQueryQuestionResult.setQuestions(queryResult);
        return pageQueryQuestionResult;
    }

    @NotNull
    private StringBuilder insertTagsIntoResult(Question question,
                                               boolean isSubQuestion,
                                               HashMap<String, String> questionMap,
                                               Map<Long, ComplexityType> complexityTypeMap,
                                               Map<Long, CoreCompetency> coreCompetencyMap,
                                               Map<Long, Grade> gradeMap,
                                               Map<Long, Source> sourceMap,
                                               Map<Long, Long> maxKnowledgePointIdMap) {

        if (!isSubQuestion) {
            questionMap.put("complexity_type", complexityTypeMap.get(question.getComplexityId()).getTypeName());
            questionMap.put("core_competency", coreCompetencyMap.get(question.getCoreCompetencyId()).getCompetencyName());
            questionMap.put("difficulty", question.getDifficulty().toString());
            questionMap.put("grade", gradeMap.get(Long.valueOf(question.getGradeId())).getName());
            questionMap.put("question_source", sourceMap.get(question.getSourceId()).getName());
            questionMap.put("knowledge_point", idToName.get(
                    maxKnowledgePointIdMap
                            .get(question.getQuestionId()).toString()
            ));

            StringBuilder knowledgePointIds = new StringBuilder();
            try {
                knowledgePointIds.append(objectMapper.writeValueAsString(question.getKnowledgePointIds()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            questionMap.put("knowledge_point_list", knowledgePointIds.toString());
            questionMap.put("question_type", String.valueOf(question.getQuestionType()));
        }

        questionMap.put("question_id", String.valueOf(question.getQuestionId()));
        questionMap.put("simple_question_type", String.valueOf(question.getSimpleQuestionType()));
        StemBlock stemBlock = question.getStemBlock();
        StringBuilder stemText = new StringBuilder(question.getStemBlock().getText());
        insertImageUrlIntoText(stemBlock, stemText);
        return stemText;
    }

    private void insertBlockTextIntoResult(Question question, HashMap<String, String> questionMap) {
        AnswerBlock answerBlock = question.getAnswerBlock();
        StringBuilder answerText = new StringBuilder();
        if (answerBlock != null) {
            answerText.append(answerBlock.getText());
            insertImageUrlIntoText(answerBlock, answerText);
        }
        questionMap.put("question_answer", answerText.toString());

        Explanation explanation = question.getExplanationBlock().getExplanation();
        StringBuilder explanationText = new StringBuilder();
        if (explanation != null) {
            explanationText.append(explanation.getText());
            insertImageUrlIntoText(explanation, explanationText);
        }

        Analysis analysis = question.getExplanationBlock().getAnalysis();
        StringBuilder analysisText = new StringBuilder();
        if (analysis != null) {
            analysisText.append(analysis.getText());
            insertImageUrlIntoText(analysis, analysisText);
        }

        FinishingTouch finishingTouch = question.getExplanationBlock().getFinishingTouch();
        StringBuilder finishingTouchText = new StringBuilder();
        if (finishingTouch != null) {
            finishingTouchText.append(finishingTouch.getText());
            insertImageUrlIntoText(finishingTouch, finishingTouchText);
        }

        questionMap.put("question_explanation", explanationText
                .append(analysisText)
                .append(finishingTouchText).toString()
        );
    }

    private void insertImageUrlIntoText(QuestionBlock block, StringBuilder text) {
        if (text.isEmpty()) {
            return;
        }
        List<Image> images = block.getImages();
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                text.insert(Math.toIntExact(image.getPosition()), image.getUrl());
            });
        }
    }
}
