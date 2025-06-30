package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.mapper.*;
import com.ht.bnu_tiku_backend.model.domain.*;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangtao
 * @description 针对表【question(习题表)】的数据库操作Service实现
 * @createDate 2025-04-17 15:23:47
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;
    @Resource
    private QuestionKnowledgeMapper questionKnowledgeMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionAnswerBlockMapper questionAnswerBlockMapper;
    @Resource
    private QuestionExplanationBlockMapper questionExplanationBlockMapper;
    @Resource
    private QuestionStemBlockMapper questionStemBlockMapper;
    @Resource
    private QuestionOptionMapper questionOptionMapper;
    @Resource
    private ImageFileMapper imageFileMapper;
    @Resource
    private ComplexityTypeMapper complexityTypeMapper;
    @Resource
    private GradeMapper gradeMapper;
    @Resource
    private CoreCompetencyMapper coreCompetencyMapper;
    @Resource
    private SourceMapper sourceMapper;

    private static void putQuestionTags(Boolean isSubQuestion,
                                        Question question, Map<Long, Source> sourceMap,
                                        Map<Long, ComplexityType> complexityTypeMap,
                                        Map<Long, CoreCompetency> coreCompetencyMap,
                                        Map<Long, Grade> gradeMap,
                                        Map<Long, String> questionIdToKnowledgePointName,
                                        Map<Long, List<Long>> questionIdToKnowledgePointIdList,
                                        HashMap<String, String> resultHashMap) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (!isSubQuestion) {
            resultHashMap.put("question_type", String.valueOf(question.getQuestionType()));
            resultHashMap.put("question_source", sourceMap.get(question.getSourceId()).getName());
            resultHashMap.put("difficulty", String.valueOf(question.getDifficulty()));
            resultHashMap.put("complexity_type", complexityTypeMap.get(question.getComplexityTypeId()).getTypeName());
            resultHashMap.put("grade", gradeMap.get(question.getGradeId()).getName());
            resultHashMap.put("knowledge_point", questionIdToKnowledgePointName.get(question.getId()));
            resultHashMap.put("knowledge_point_list", objectMapper.writeValueAsString(questionIdToKnowledgePointIdList.get(question.getId())));
            resultHashMap.put("core_competency", coreCompetencyMap.get(question.getCoreCompetencyId()).getCompetencyName());
        }
        resultHashMap.put("question_id", String.valueOf(question.getId()));
        resultHashMap.put("simple_question_type", question.getSimpleQuestionType().toString());
    }

    /**
     * 根据知识点编号查询习题
     *
     * @param knowledgePointIdlist
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public List<Map<String, String>> queryQuestionsByKnowledgePointIds(List<Long> knowledgePointIdlist) throws JsonProcessingException {
        //System.out.println(knowledgePointIdlist);
        if (knowledgePointIdlist.isEmpty()) {
            // 查询结果集合
            return new ArrayList<>();
        }
        // 2. 在知识点试题关联表中，查询知识点下所有试题的id
        QueryWrapper<QuestionKnowledge> questionKnowledgeQueryWrapper = new QueryWrapper<>();
        questionKnowledgeQueryWrapper.in("knowledge_point_id", knowledgePointIdlist);
        Set<Long> questionIdSet = questionKnowledgeMapper.selectList(questionKnowledgeQueryWrapper).stream().map(QuestionKnowledge::getQuestionId).collect(Collectors.toSet());
        List<Long> questionIdList = questionIdSet.stream().toList();
        return queryQuestionsByQuestionIds(questionIdList);
    }

    public List<Map<String, String>> queryQuestionsByQuestionIds(List<Long> questionIdList) throws JsonProcessingException {
        List<Map<String, String>> results = new ArrayList<>(); // 查询结果集合
        // 1. 预先批量查询标签信息
        // 1.1 根据习题id查询所有的习题元信息
        long startTime = System.currentTimeMillis();
        List<Question> questionInfoList = questionMapper.selectBatchIds(questionIdList);
        Map<Long, Question> questionInfoMap = questionInfoList.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        // 1.2 查询习题来源
        List<Long> sourceIds = questionInfoList.stream().map(Question::getSourceId).toList();
        Map<Long, Source> sourceMap = sourceMapper.selectBatchIds(sourceIds)
                .stream().collect(Collectors.toMap(Source::getId, s -> s));
        // 1.2 查询综合类型
        List<Long> complexityTypeIds = questionInfoList.stream().map(Question::getComplexityTypeId).toList();
        Map<Long, ComplexityType> complexityTypeMap = complexityTypeMapper.selectBatchIds(complexityTypeIds)
                .stream().collect(Collectors.toMap(ComplexityType::getId, c -> c));
        // 1.3 查询核心素养
        List<Long> coreCompetencyIds = questionInfoList.stream().map(Question::getCoreCompetencyId).toList();
        Map<Long, CoreCompetency> coreCompetencyMap = coreCompetencyMapper.selectBatchIds(coreCompetencyIds)
                .stream().collect(Collectors.toMap(CoreCompetency::getId, c -> c));
        // 1.4 查询年级
        List<Long> gradeIds = questionInfoList.stream().map(Question::getGradeId).toList();
        Map<Long, Grade> gradeMap = gradeMapper.selectBatchIds(gradeIds)
                .stream().collect(Collectors.toMap(Grade::getId, g -> g));
        // 1.4 查询习题关联的知识点
        QueryWrapper<QuestionKnowledge> questionKnowledgeWrapper = new QueryWrapper<>();
        questionKnowledgeWrapper.in("question_id", questionIdList);
        List<QuestionKnowledge> questionKnowledgeList = questionKnowledgeMapper.selectList(questionKnowledgeWrapper);
        // 1.4.1 习题ID与知识点映射
        Map<Long, List<Long>> questionIdToKnowledgePointIdList = questionKnowledgeList.stream()
                .collect(Collectors.groupingBy(
                        QuestionKnowledge::getQuestionId,
                        Collectors.mapping(QuestionKnowledge::getKnowledgePointId, Collectors.toList())
                ));
        // 1.4.2 习题ID与最底层知识点映射
        Map<Long, Long> questionIdToMaxKnowledgePointId = questionIdToKnowledgePointIdList.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Collections.max(entry.getValue())
                ));
        // 1.4.3 习题ID与知识点名映射
        Map<Long, String> questionIdToKnowledgePointName = questionIdToMaxKnowledgePointId.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // question_id
                        entry -> {
                            KnowledgePoint kp = knowledgePointMapper.selectById(entry.getKey());
                            return kp != null ? kp.getName() : ""; // 如果找不到知识点名就返回空串
                        }
                ));

        // 1.5 构建父题与子题的关联映射
        List<Long> compositeQuestionIds = questionInfoList.stream()
                .filter(q -> q.getQuestionType() != 0) // 题型不为简单题
                .map(Question::getId)
                .toList();
        Map<Long, List<Question>> parentIdToSubQuestions = null;
        if (!compositeQuestionIds.isEmpty()) {
            QueryWrapper<Question> subQuestionWrapper = new QueryWrapper<>();
            subQuestionWrapper.in("parent_id", compositeQuestionIds);
            List<Question> subQuestions = questionMapper.selectList(subQuestionWrapper);

            // 构建 Map<复合题ID, List<小题>>
            parentIdToSubQuestions = subQuestions.stream()
                    .collect(Collectors.groupingBy(Question::getParentId));
        }

        long endTime1 = System.currentTimeMillis();

        log.info("查询标签信息花费的时间：{}ms", (endTime1 - startTime));

        // 2. 遍历所有的习题ID，组装完整习题内容
        for (Long questionId : questionIdList) {
            // 2.1 获取试题习题类型
            Question question = questionInfoMap.get(questionId);
            Integer questionType = question.getQuestionType();
            // 2.2 判断试题的类型
            if (questionType == 0) {
                //2.2.1 如果是简单题，直接查询题干块、答案块、解析块
                HashMap<String, String> simpleQuestionResult = SelectSimpleQuestion(
                        false,
                        question,
                        sourceMap,
                        complexityTypeMap,
                        coreCompetencyMap,
                        gradeMap,
                        questionIdToKnowledgePointName,
                        questionIdToKnowledgePointIdList);
                results.add(simpleQuestionResult);
            } else {
                // 2.2.2 如果是复合题，需要去查询所有小题，再查询小题的题干、答案、解析块
                HashMap<String, String> compositeQuestionResult = new HashMap<>();
                // 2.2.2.1 将之前查询到的标签插入到结果中
                putQuestionTags(
                        false,
                        question,
                        sourceMap,
                        complexityTypeMap,
                        coreCompetencyMap,
                        gradeMap,
                        questionIdToKnowledgePointName,
                        questionIdToKnowledgePointIdList,
                        compositeQuestionResult);
                // 2.2.2.2 组装复合题的题干（文本+图像html标签）
                StringBuilder compositeQuestionStemStringBuilder = new StringBuilder();
                QueryWrapper<QuestionStemBlock> compositeQuestionStemBlockQueryWrapper = new QueryWrapper<>();
                compositeQuestionStemBlockQueryWrapper.eq("question_id", questionId);
                // 2.2.2.2.1 先将文本拼接起来，并抽取图像列表（含图像url和图像在文本中的位置信息）
                List<String> compositeQuestionStemImageStringList = questionStemBlockMapper
                        .selectList(compositeQuestionStemBlockQueryWrapper)
                        .stream().map(compositeQuestionStemBlock -> {
                            if (compositeQuestionStemBlock.getContentType() == 0) {
                                compositeQuestionStemStringBuilder.append(compositeQuestionStemBlock.getTextContent());
                                return null;
                            }
                            return compositeQuestionStemBlock.getImageFileId().toString() + ":" +
                                    compositeQuestionStemBlock.getPosition();
                        }).toList();
                // 2.2.2.2.2 如果图像不为空，则将图像插入到文本中
                if (!compositeQuestionStemImageStringList.isEmpty()) {
                    insertImageUrlToQuestionBlockString(compositeQuestionStemImageStringList, compositeQuestionStemStringBuilder);
                }
                compositeQuestionResult.put("composite_question_stem", compositeQuestionStemStringBuilder.toString());
                // 2.2.2.3 组装复合题的小题，这里默认小题是简单题
                List<Question> subQuestions = null;
                if (parentIdToSubQuestions != null) {
                    // 2.2.2.3.1 根据之前建立的映射获取复合题的所有小题ID
                    subQuestions = parentIdToSubQuestions.get(questionId);
                }
                ArrayList<HashMap<String, String>> subQuestionResults = new ArrayList<>();
                // 2.2.2.3.2 直接走查简单题的流程
                if (subQuestions != null) {
                    for (Question subQuestion : subQuestions) {
                        HashMap<String, String> subQuestionResult = SelectSimpleQuestion(
                                true,
                                subQuestion,
                                sourceMap,
                                complexityTypeMap,
                                coreCompetencyMap,
                                gradeMap,
                                questionIdToKnowledgePointName,
                                questionIdToKnowledgePointIdList);
                        subQuestionResults.add(subQuestionResult);
                    }
                }
                String subQuestionResultStrings = new ObjectMapper().writeValueAsString(subQuestionResults);
                compositeQuestionResult.put("sub_questions", subQuestionResultStrings);
                results.add(compositeQuestionResult);
            }
        }
        long endTime2 = System.currentTimeMillis();
        log.info("花费总时间{}ms", (endTime2 - startTime));
        return results;
    }

    @Override
    public List<Map<String, String>> queryQuestionsByKnowledgePoint(String name) throws JsonProcessingException {
        QueryWrapper<KnowledgePoint> knowledgePointQueryWrapper = new QueryWrapper<>();
        if (name.equals("beforeMount")) {
            name = "";
            knowledgePointQueryWrapper.like("name", name);
        } else {
            knowledgePointQueryWrapper.eq("name", name);
        }

        List<KnowledgePoint> knowledgePoints = knowledgePointMapper.selectList(knowledgePointQueryWrapper);
        //System.out.println(knowledgePoints.size());
        List<Long> knowledgePointIds = knowledgePoints.stream().map(KnowledgePoint::getId).collect(Collectors.toList());
        return queryQuestionsByKnowledgePointIds(knowledgePointIds);
    }

    @Override
    public List<Map<String, String>> queryQuestionsBySource(String name) {
        return List.of();
    }

    @Override
    public List<Map<String, String>> queryQuestionsByGrade(String name) {
        return List.of();
    }

    @Override
    public List<Map<String, String>> queryQuestionsByDifficulty(String name) {
        return List.of();
    }

    @Override
    public void deleteQuestion(Long id) {

    }

    @Override
    public PageQueryQuestionResult pageQueryQuestionsByKnowledgePoint(String name, Long pageNumber, Long pageSize) throws JsonProcessingException {
        QueryWrapper<KnowledgePoint> knowledgePointQueryWrapper = new QueryWrapper<>();
//        System.out.println(name);
        if (!name.contains("beforeMount")) {
            //System.out.println("************");
            knowledgePointQueryWrapper.eq("name", name.strip());
        }
        List<KnowledgePoint> knowledgePoints = knowledgePointMapper.selectList(knowledgePointQueryWrapper);
        List<Long> knowledgePointIds = knowledgePoints.stream().map(KnowledgePoint::getId).toList();
//        List<Long> knowledgePointIds = new ArrayList<>();
//        knowledgePointIds.add(2L);
        //System.out.println("knowledgePointIds:"+knowledgePointIds);
        return pageQueryQuestionsByIds(knowledgePointIds, pageNumber, pageSize);
    }

    public PageQueryQuestionResult pageQueryQuestionsByIds(List<Long> knowledgePointIdlist, Long pageNumber, Long pageSize) throws JsonProcessingException {
//        QueryWrapper<QuestionKnowledge> questionKnowledgeQueryWrapper = new QueryWrapper<>();
//        questionKnowledgeQueryWrapper.in("knowledge_point_id", knowledgePointIdlist).select("DISTINCT question_id");;
//        Page<QuestionKnowledge> page = new Page<>(pageNumber, pageSize);// 条件：question_type = 'simple'
//        Page<QuestionKnowledge> questionKnowledgePage = questionKnowledgeMapper.selectPage(page, questionKnowledgeQueryWrapper);
//        List<Long> questionIdlist = questionKnowledgePage.getRecords().stream().map(QuestionKnowledge::getQuestionId).toList();
//        List<Map<String, String>> questions = queryQuestionsByQuestionIds(questionIdlist);
//        PageQueryQuestionResult result = new PageQueryQuestionResult();
//        result.setPageNo(pageNumber);
//        result.setPageSize(pageSize);
//        result.setTotalCount(questionKnowledgePage.getTotal());
//        result.setQuestions(questions);
//        System.out.println(result.getQuestions().size());
//        System.out.println(questionIdlist.size());
        // Step 1: 查询所有符合条件的 question_id（去重）
        QueryWrapper<QuestionKnowledge> wrapper = new QueryWrapper<>();
        wrapper.in("knowledge_point_id", knowledgePointIdlist);
        //System.out.println(knowledgePointIdlist);
        List<Long> allQuestionIds = questionKnowledgeMapper.selectList(wrapper).stream()
                .map(QuestionKnowledge::getQuestionId)
                .distinct()
                .toList();
        // Step 2: 在 question_id 上做分页
        int fromIndex = (int) ((pageNumber - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize.intValue(), allQuestionIds.size());
        List<Long> pagedQuestionIds = allQuestionIds.subList(fromIndex, toIndex);

        // Step 3: 查询题目详情
        List<Map<String, String>> questions = queryQuestionsByQuestionIds(pagedQuestionIds);

        // Step 4: 封装结果
        PageQueryQuestionResult result = new PageQueryQuestionResult();
        result.setPageNo(pageNumber);
        result.setPageSize(pageSize);
        result.setTotalCount((long) allQuestionIds.size());
        result.setQuestions(questions);
//          System.out.println(result);
//        System.out.println(questions.size());
//        System.out.println(pagedQuestionIds.size());
//        System.out.println(allQuestionIds.size());
//        System.out.println(allQuestionIds.toString());
//        System.out.println(fromIndex+"-"+toIndex);
        return result;
    }

    private HashMap<String, String> SelectSimpleQuestion(Boolean isSubQuestion,
                                                         Question question,
                                                         Map<Long, Source> sourceMap,
                                                         Map<Long, ComplexityType> complexityTypeMap,
                                                         Map<Long, CoreCompetency> coreCompetencyMap,
                                                         Map<Long, Grade> gradeMap, Map<Long, String> questionIdToKnowledgePointName,
                                                         Map<Long, List<Long>> questionIdToKnowledgePointIdList)
            throws JsonProcessingException {
        StringBuilder questionStemStringBuilder = new StringBuilder();
        QueryWrapper<QuestionStemBlock> questionStemBlockQueryWrapper = new QueryWrapper<>();
        Long questionId = question.getId();
        questionStemBlockQueryWrapper.eq("question_id", questionId);

        // 1. 查询题干
        // 1.1 拼接题干文本块，抽取图像列表
        List<String> stemImgStringlist = questionStemBlockMapper.selectList(questionStemBlockQueryWrapper).stream()
                .map(questionStemBlock -> {
                    if (questionStemBlock.getContentType() == 0) {
                        questionStemStringBuilder.append(questionStemBlock.getTextContent());
                        return null;
                    } else {
                        return questionStemBlock.getImageFileId().toString() + ":" + questionStemBlock.getPosition();
                    }
                }).toList();
        // 1.2 将图像插入题干文本
        if (!stemImgStringlist.isEmpty()) {
            insertImageUrlToQuestionBlockString(stemImgStringlist, questionStemStringBuilder);
        }

        if (question.getSimpleQuestionType() == 0) {
            QueryWrapper<QuestionOption> questionOptionQueryWrapper = new QueryWrapper<>();
            questionOptionQueryWrapper.eq("question_id", questionId);
            List<QuestionOption> questionOptions = questionOptionMapper.selectList(questionOptionQueryWrapper);
            for (QuestionOption questionOption : questionOptions) {
                StringBuilder optionStringBuilder = new StringBuilder(questionOption.getContent());
                String imageFileIds = (String) questionOption.getImageFileIds();
                String imagePositions = (String) questionOption.getImagePositions();
                if (StringUtils.isNotBlank(imageFileIds)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<String> imgIdlist = objectMapper.readValue(imageFileIds, List.class);
                    List<String> imgPositionList = objectMapper.readValue(imagePositions, List.class);
                    for (int i = 0; i < imgIdlist.size(); i++) {
                        QueryWrapper<ImageFile> imageFileQueryWrapper = new QueryWrapper<>();
                        imageFileQueryWrapper.eq("image_id", imgIdlist.get(i));
                        ImageFile imageFile = imageFileMapper.selectOne(imageFileQueryWrapper);
                        optionStringBuilder.insert(Integer.parseInt(imgPositionList.get(i)), imageFile.getImageUrl());
                    }
                }
                questionStemStringBuilder.append(questionOption.getLabel()).append(": ").append(optionStringBuilder).append("   ");
            }
        }

        // 2. 查询答案
        // 2.1 拼接答案文本，抽取答案的图像列表
        StringBuilder questionAnswerStringBuilder = new StringBuilder();
        QueryWrapper<QuestionAnswerBlock> questionAnswerBlockQueryWrapper = new QueryWrapper<>();
        questionAnswerBlockQueryWrapper.eq("question_id", questionId);
        List<String> answerImageStringList = questionAnswerBlockMapper
                .selectList(questionAnswerBlockQueryWrapper)
                .stream().map(questionAnswerBlock -> {
                    if (questionAnswerBlock.getContentType() == 0) {
                        questionAnswerStringBuilder.append(questionAnswerBlock.getAnswerText());
                        return null;
                    } else {
                        return questionAnswerBlock.getImageFileId().toString() + ":" + questionAnswerBlock.getPosition();
                    }
                }).toList();
        // 2.2 将图像插入到文本当中
        if (!answerImageStringList.isEmpty()) {
            insertImageUrlToQuestionBlockString(answerImageStringList, questionAnswerStringBuilder);
        }

        // 3. 查询解析
        // 3.1 拼接解析文本，抽取答案的图像列表
        StringBuilder questionExplanationStringBuilder = new StringBuilder();
        QueryWrapper<QuestionExplanationBlock> questionExplanationBlockQueryWrapper = new QueryWrapper<>();
        questionExplanationBlockQueryWrapper.eq("question_id", questionId);
        List<String> explanationImageStringList = questionExplanationBlockMapper
                .selectList(questionExplanationBlockQueryWrapper)
                .stream().map(questionExplanationBlock -> {
                    if (questionExplanationBlock.getContentType() == 0) {
                        if (questionExplanationBlock.getExplanationType() == 0)
                            questionExplanationStringBuilder
                                    .append("解析：<br/>")
                                    .append(questionExplanationBlock.getExplanationText())
                                    .append("<br/>");
                        else {
                            questionExplanationStringBuilder
                                    .append("详解：<br/>")
                                    .append(questionExplanationBlock.getExplanationText())
                                    .append("<br/>");
                        }
                        return null;
                    } else {
                        return questionExplanationBlock.getImageFileId().toString() + ":" + questionExplanationBlock.getPosition();
                    }
                }).toList();
        // 3.2 将图像插入到文本当中
        if (!explanationImageStringList.isEmpty()) {
            insertImageUrlToQuestionBlockString(explanationImageStringList, questionExplanationStringBuilder);
        }

        // 3.3 将习题的标签插入结果中
        HashMap<String, String> resultHashMap = new HashMap<>();
        putQuestionTags(
                isSubQuestion,
                question,
                sourceMap,
                complexityTypeMap,
                coreCompetencyMap,
                gradeMap,
                questionIdToKnowledgePointName,
                questionIdToKnowledgePointIdList,
                resultHashMap);
        resultHashMap.put("stem", questionStemStringBuilder.toString());
        resultHashMap.put("question_answer", questionAnswerStringBuilder.toString());
        resultHashMap.put("question_explanation", questionExplanationStringBuilder.toString());
        return resultHashMap;
    }

    private void insertImageUrlToQuestionBlockString(List<String> imgStringlist, StringBuilder questionStemStringBuilder) {
        imgStringlist.forEach(imgString -> {
            if (StringUtils.isNotBlank(imgString)) {
                String[] splits = imgString.split(":");
                String imgFileId = splits[0];
                String imgPosition = splits[1];
                QueryWrapper<ImageFile> imageFileQueryWrapper = new QueryWrapper<>();
                imageFileQueryWrapper.eq("id", imgFileId);
                ImageFile imageFile = imageFileMapper.selectOne(imageFileQueryWrapper);
                questionStemStringBuilder.insert(Integer.parseInt(imgPosition), imageFile.getImageUrl());
            }
        });
    }
}




