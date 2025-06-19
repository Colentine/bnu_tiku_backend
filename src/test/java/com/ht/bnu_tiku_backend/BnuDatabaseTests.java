package com.ht.bnu_tiku_backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.mapper.*;
import com.ht.bnu_tiku_backend.model.domain.*;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@SpringBootTest
@RunWith(SpringRunner.class)
public class BnuDatabaseTests {
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
    private QuestionService questionService;

    /**
     * @description: 向数据库插入两个用户
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 14:01
     */
    @Test
    public void userInsertTest() {
        User user = new User();
        user.setUserAccount("000001");
        user.setUserName("kkkkk");
        user.setUserRealName("王大柱");
        user.setUserPassword("123456");
        user.setUserRole(0);
        user.setSchoolId(0);
        user.setEmail("xxxxxxxx@qq.com");
        user.setAvatarUrl("");

        User user1 = new User();
        user1.setUserAccount("000002");
        user1.setUserName("kkkkz");
        user1.setUserRealName("王小柱");
        user1.setUserPassword("12344");
        user1.setUserRole(0);
        user1.setSchoolId(2);
        user1.setEmail("yyyyyyy@qq.com");
        user1.setAvatarUrl("");

        userMapper.insert(user);
        userMapper.insert(user1);
    }

    /**
     * @description: 向数据库插入知识点数据
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 14:02
     */
    @Test
    public void knowledgePointsInsert() throws IOException {
        String path = "resources/KnowledgeTree/xkb_knowledge_tree.json";

        ObjectMapper mapper = new ObjectMapper();

        List<KnowledgePoint> points = mapper.readValue(
                new File(path),
                new TypeReference<List<KnowledgePoint>>() {
                }
        );

        // 批量插入（逐条 insert 可使用批处理优化）
        knowledgePointMapper.insert(points);

        System.out.println("导入成功，共导入 " + points.size() + " 条知识点。");
    }

    /**
     * @description: 查询数据库知识点数量
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 14:03
     */
    @Test
    public void knowledgePointCountTest() {
        Long l = knowledgePointMapper.selectCount(null);
        System.out.println(l);
    }

    /**
     * @description: 知识点id查询
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 14:03
     */
    @Test
    public void knowledgePointsSelectByNameTest() {
        QueryWrapper<KnowledgePoint> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("name", "图形的性质").or().eq("name", "三角形").or().eq("name", "勾股定理及逆定理").or().eq("name", "勾股定理").or().eq("name", "已知两点坐标求两点距离");
        knowledgePointMapper.selectList(objectQueryWrapper).forEach(knowledgePoint -> {
            System.out.println(knowledgePoint.getName() + ":" + knowledgePoint.getId());
        });
    }


    /**
     * @description: 知识点id查题目
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 19:35
     */
    @Test
    public void knowledgePointsSelectByIdTest() {
        QueryWrapper<QuestionKnowledge> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.in("knowledge_point_id", "5502");
        questionKnowledgeMapper.selectList(objectQueryWrapper).forEach(questionKnowledge -> {
            System.out.println(questionKnowledge.toString());
        });
    }

    /**
     * @description: 根据用户id查询用户
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 14:03
     */
    @Test
    public void userSelectTest() {
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }

    @Test
    public void selectOneSimpleQuestionByKnowledgePointTest() {
        QueryWrapper<KnowledgePoint> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("name", "正负数的定义");
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectOne(objectQueryWrapper);

        QueryWrapper<QuestionKnowledge> questionKnowledgeQueryWrapper = new QueryWrapper<>();
        questionKnowledgeQueryWrapper.eq("knowledge_point_id", knowledgePoint.getId());
        QuestionKnowledge questionKnowledge = questionKnowledgeMapper.selectOne(questionKnowledgeQueryWrapper);
        Long questionId = questionKnowledge.getQuestionId();
        Question questionTagInfo = questionMapper.selectById(questionId);
        System.out.println(questionTagInfo.toString());

        QueryWrapper<QuestionStemBlock> questionStemBlockQueryWrapper = new QueryWrapper<>();
        questionStemBlockQueryWrapper.eq("question_id", questionId);
        QuestionStemBlock questionStemBlock = questionStemBlockMapper.selectOne(questionStemBlockQueryWrapper);
        String textContent = questionStemBlock.getTextContent();
        System.out.println(textContent);

        QueryWrapper<QuestionOption> questionOptionQueryWrapper = new QueryWrapper<>();
        questionOptionQueryWrapper.eq("question_id", questionId);
        questionOptionMapper.selectList(questionOptionQueryWrapper).forEach(option -> {
            System.out.println(option.getLabel() + ": " + option.getContent());
        });

        QueryWrapper<QuestionAnswerBlock> questionAnswerBlockQueryWrapper = new QueryWrapper<>();
        questionAnswerBlockQueryWrapper.eq("question_id", questionId);
        QuestionAnswerBlock questionAnswerBlock = questionAnswerBlockMapper.selectOne(questionAnswerBlockQueryWrapper);
        String answerText = questionAnswerBlock.getAnswerText();
        System.out.println(answerText);

        QueryWrapper<QuestionExplanationBlock> questionExplanationBlockQueryWrapper = new QueryWrapper<>();
        questionExplanationBlockQueryWrapper.eq("question_id", questionId);
        List<QuestionExplanationBlock> questionExplanationBlocks = questionExplanationBlockMapper.selectList(questionExplanationBlockQueryWrapper);
        for (QuestionExplanationBlock questionExplanationBlock : questionExplanationBlocks) {
            System.out.println(questionExplanationBlock.getExplanationText());
        }
    }

    /**
     * @description: 知识点查询试题
     * @param:
     * @return:
     * @author huangtao
     * @date: 2025/4/17 14:03
     */
    @Test
    public void selectQuestionsByKnowledgePointTest() throws JsonProcessingException {
        // 1. 在知识点表中，根据知识点名字查询知识点id
        String knowledgePointName = "数与式";
        QueryWrapper<KnowledgePoint> knowledgePointQueryWrapper = new QueryWrapper<>();
        knowledgePointQueryWrapper.eq("name", knowledgePointName);
        List<KnowledgePoint> knowledgePoints = knowledgePointMapper.selectList(knowledgePointQueryWrapper);
        List<Long> knowledgePointIdlist = knowledgePoints.stream().map(KnowledgePoint::getId).toList();
        System.out.println(knowledgePointIdlist);
        // 2. 在知识点试题关联表中，查询知识点下所有试题的id
        QueryWrapper<QuestionKnowledge> questionKnowledgeQueryWrapper = new QueryWrapper<>();
        questionKnowledgeQueryWrapper.in("knowledge_point_id", knowledgePointIdlist);
        Set<Long> questionIdSet = questionKnowledgeMapper.selectList(questionKnowledgeQueryWrapper).stream().map(QuestionKnowledge::getQuestionId).collect(Collectors.toSet());
        List<Long> questionIdList = questionIdSet.stream().toList();
        // 3. 遍历所有的试题id
        List<Map<String, String>> results = new ArrayList<>();// 查询结果集合
        Map<String, String> queryCount = new HashMap<>();
        queryCount.put("query_count", String.valueOf(questionIdList.size()));
        results.add(queryCount);
        for (Long questionId : questionIdList) {
            // 3.1 在试题总表中查询试题的标签信息（题目类型等）
            Question question = questionMapper.selectById(questionId);
            Integer questionType = question.getQuestionType();
            // 3.2 查询与题目关联的所有知识点
            questionKnowledgeQueryWrapper.clear();
            questionKnowledgeQueryWrapper.eq("question_id", questionId);
            List<Long> knowledgeList = questionKnowledgeMapper.selectList(questionKnowledgeQueryWrapper).stream().map(QuestionKnowledge::getKnowledgePointId).toList();
            // 3.4 判断试题的类型
            if (questionType == 0) { // 如果是简单题，直接查询题干块、答案块、解析块
                HashMap<String, String> simpleQuestionResult = SelectSimpleQuestion(false, questionId, question, knowledgeList, results);
                results.add(simpleQuestionResult);
            } else { // 如果是复合题，需要去查询所有小题，再查询小题的题干、答案、解析块
                StringBuilder compositeQuestionStemStringBuilder = new StringBuilder();
                QueryWrapper<QuestionStemBlock> compositeQuestionStemBlockQueryWrapper = new QueryWrapper<>();
                compositeQuestionStemBlockQueryWrapper.eq("question_id", questionId);
                List<String> compositeQuestionStemImageStringList = questionStemBlockMapper
                        .selectList(compositeQuestionStemBlockQueryWrapper)
                        .stream().map(compositeQuestionStemBlock -> {
                            if (compositeQuestionStemBlock.getContentType() == 0) {
                                compositeQuestionStemStringBuilder.append(compositeQuestionStemBlock.getTextContent());
                                return null;
                            } else {
                                return compositeQuestionStemBlock.getImageFileId().toString() + ":" + compositeQuestionStemBlock.getPosition();
                            }
                        }).toList();
                if (!compositeQuestionStemImageStringList.isEmpty()) {
                    insertImageUrlToQuestionBlockString(compositeQuestionStemImageStringList, compositeQuestionStemStringBuilder);
                }

                QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
                questionQueryWrapper.eq("parent_id", questionId);
                List<Question> questions = questionMapper.selectList(questionQueryWrapper);
                HashMap<String, String> compositeQuestionResult = new HashMap<>();
                compositeQuestionResult.put("question_type", String.valueOf(question.getQuestionType()));
                compositeQuestionResult.put("difficulty", String.valueOf(question.getDifficulty()));
                compositeQuestionResult.put("complexity_type", complexityTypeMapper.selectById(question.getComplexityTypeId()).getTypeName());
                compositeQuestionResult.put("grade", gradeMapper.selectById(question.getGradeId()).getName());
                compositeQuestionResult.put("knowledge_point", knowledgeList.toString());
                compositeQuestionResult.put("core_competency", coreCompetencyMapper.selectById(question.getCoreCompetencyId()).getCompetencyName());
                compositeQuestionResult.put("composite_question_stem", compositeQuestionStemStringBuilder.toString());
                int subQuestionIndex = 1;
                for (Question subQuestion : questions) {
                    HashMap<String, String> subQuestionResult = SelectSimpleQuestion(true, subQuestion.getId(), subQuestion, knowledgeList, results);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String subQuestionResultString = objectMapper.writeValueAsString(subQuestionResult);
                    compositeQuestionResult.put("小题" + String.valueOf(subQuestionIndex), subQuestionResultString);
                    subQuestionIndex += 1;
                }
                results.add(compositeQuestionResult);
            }
        }
        System.out.println("查询知识点：" + knowledgePointName + "," + "查询集合（大小：" + String.valueOf(results.size()) + "）：" + results);
    }

    private HashMap<String, String> SelectSimpleQuestion(Boolean isSubQuestion, Long questionId, Question question, List<Long> knowledgeList, List<Map<String, String>> results) throws JsonProcessingException {
        StringBuilder questionStemStringBuilder = new StringBuilder();
        QueryWrapper<QuestionStemBlock> questionStemBlockQueryWrapper = new QueryWrapper<>();
        questionStemBlockQueryWrapper.eq("question_id", questionId);

        // 查询题干

        List<String> stemImgStringlist = questionStemBlockMapper.selectList(questionStemBlockQueryWrapper).stream()
                .map(questionStemBlock -> {
                    if (questionStemBlock.getContentType() == 0) {
                        questionStemStringBuilder.append(questionStemBlock.getTextContent());
                        return null;
                    } else {
                        return questionStemBlock.getImageFileId().toString() + ":" + questionStemBlock.getPosition();
                    }
                }).toList();
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
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(imageFileIds)) {
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

        //查询答案
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
        if (!answerImageStringList.isEmpty()) {
            insertImageUrlToQuestionBlockString(answerImageStringList, questionAnswerStringBuilder);
        }

        //查询解析
        StringBuilder questionExplanationStringBuilder = new StringBuilder();
        QueryWrapper<QuestionExplanationBlock> questionExplanationBlockQueryWrapper = new QueryWrapper<>();
        questionExplanationBlockQueryWrapper.eq("question_id", questionId);
        List<String> explanationImageStringList = questionExplanationBlockMapper
                .selectList(questionExplanationBlockQueryWrapper)
                .stream().map(questionExplanationBlock -> {
                    if (questionExplanationBlock.getContentType() == 0) {
                        questionExplanationStringBuilder.append(questionExplanationBlock.getExplanationText());
                        return null;
                    } else {
                        return questionExplanationBlock.getImageFileId().toString() + ":" + questionExplanationBlock.getPosition();
                    }
                }).toList();
        if (!explanationImageStringList.isEmpty()) {
            insertImageUrlToQuestionBlockString(explanationImageStringList, questionExplanationStringBuilder);
        }

        HashMap<String, String> resultHashMap = new HashMap<>();
        if (!isSubQuestion) {
            resultHashMap.put("question_type", String.valueOf(question.getQuestionType()));
            resultHashMap.put("difficulty", String.valueOf(question.getDifficulty()));
            resultHashMap.put("complexity_type", complexityTypeMapper.selectById(question.getComplexityTypeId()).getTypeName());
            resultHashMap.put("grade", gradeMapper.selectById(question.getGradeId()).getName());
            resultHashMap.put("knowledge_point", knowledgeList.toString());
            resultHashMap.put("core_competency", coreCompetencyMapper.selectById(question.getCoreCompetencyId()).getCompetencyName());
        }
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

    @Test
    public void excelDataAutoInsertTest() throws IOException {
        String path = "KnowledgeTree/xkb_node_to_id.json";
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> knowledgeToId = objectMapper.readValue(
                is,
                new TypeReference<>() {
                }
        );
        System.out.println(knowledgeToId.get("数"));

        String filePath = "data/tiku/middle_school_1121.xlsx";
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Random random = new Random();
        AtomicReference<Long> questionId = new AtomicReference<>(18L);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            List<String> levelFiveKps = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    continue;
                }
                cell.setCellType(CellType.STRING);
                String name = cell.getStringCellValue().trim();
                //System.out.println(name);
                levelFiveKps.add(name);
            }
            Cell cell = row.getCell(5);
            cell.setCellType(CellType.STRING);
            String questionListString = cell.getStringCellValue().trim();
            objectMapper
                    .readValue(questionListString, new TypeReference<List<Map<String, String>>>() {
                    }).stream().forEach(questionMap -> {
                        int questionType = 0;
                        int simpleQuestionType = random.nextInt(0, 4);
                        long complexityTypeId = random.nextInt(1, 10);
                        long gradeId = random.nextInt(1, 6);
                        long sourceId = random.nextInt(1, 10);
                        double difficulty = Math.random();
                        long coreCompetencyId = random.nextInt(1, 10);
                        Question questionMetaInfo = new Question();
                        questionMetaInfo.setQuestionType(questionType);
                        questionMetaInfo.setSimpleQuestionType(simpleQuestionType);
                        questionMetaInfo.setGradeId(gradeId);
                        questionMetaInfo.setSourceId(sourceId);
                        questionMetaInfo.setDifficulty(difficulty);
                        questionMetaInfo.setComplexityTypeId(complexityTypeId);
                        questionMetaInfo.setCoreCompetencyId(coreCompetencyId);
                        questionMetaInfo.setCreatedBy(1L);
                        questionMapper.insert(questionMetaInfo);

                        for (String kp : levelFiveKps) {
                            String knowledgeId = knowledgeToId.get(kp);
                            QuestionKnowledge questionKnowledge = new QuestionKnowledge();
                            questionKnowledge.setQuestionId(questionId.get());
                            questionKnowledge.setKnowledgePointId(Long.parseLong(knowledgeId));
                            questionKnowledgeMapper.insert(questionKnowledge);
                        }
                        QuestionStemBlock questionStemBlock = new QuestionStemBlock();
                        questionStemBlock.setQuestionId(questionId.get());
                        questionStemBlock.setContentType(0);
                        questionStemBlock.setPosition(0);
                        questionStemBlock.setTextContent(questionMap.get("content"));
                        questionStemBlockMapper.insert(questionStemBlock);

                        QuestionAnswerBlock questionAnswerBlock = new QuestionAnswerBlock();
                        questionAnswerBlock.setQuestionId(questionId.get());
                        questionAnswerBlock.setContentType(0);
                        questionAnswerBlock.setInteractiveIndex(0L);
                        questionAnswerBlock.setAnswerText(questionMap.get("answer"));
                        questionAnswerBlock.setPosition(0);
                        questionAnswerBlockMapper.insert(questionAnswerBlock);

                        QuestionExplanationBlock questionExplanationBlock = new QuestionExplanationBlock();
                        questionExplanationBlock.setQuestionId(questionId.get());
                        questionExplanationBlock.setExplanationType(0);
                        questionExplanationBlock.setContentType(0);
                        questionExplanationBlock.setExplanationText(questionMap.get("analysis"));
                        questionExplanationBlock.setInteractiveIndex(0L);
                        questionExplanationBlock.setPosition(0);
                        questionExplanationBlockMapper.insert(questionExplanationBlock);

                        questionId.getAndSet(questionId.get() + 1);
                    });
        }
    }

    @Test
    public void pagedQueryQuestionTest() throws IOException {
        String kpName = "有理数";
        long l = 1L;
        long l1 = 10L;
        PageQueryQuestionResult result = questionService.pageQueryQuestionsByKnowledgePoint(kpName, l, l1);
//        System.out.println(result.toString());
    }

    @Test
    public void excelDataAutoUpdateTest() {
        AtomicReference<Long> questionId = new AtomicReference<>(18L);
        questionId.getAndSet(questionId.get() + 1);
        System.out.println(questionId.get());
    }

    @Test
    public void findAllQuestionTest() {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
    }

    @Test
    public void logicDeleteUserTest() {
        userMapper.deleteById(16L);
    }

    @Test
    public void md5EncryptTest() {
        String s = SecureUtil.md5("12344");
        System.out.println(s);
    }
}
