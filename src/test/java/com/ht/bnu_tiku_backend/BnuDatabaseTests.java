package com.ht.bnu_tiku_backend;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.mapper.*;
import com.ht.bnu_tiku_backend.model.domain.*;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    /**
     * 向数据库插入两个用户
     * @author: ht
     */
    @Test
    public void userInsertTest(){
        User user = new User();
        user.setId(1L);
        user.setUserAccount("00001");
        user.setUserName("kkkkk");
        user.setUserRealName("王大柱");
        user.setUserPassword("123456");
        user.setRole(1);
        user.setSchoolId(0);
        user.setEmail("xxxxxxxx@qq.com");
        user.setAvatarUrl("");

        User user1 = new User();
        user1.setUserAccount("00002");
        user1.setUserName("kkkkz");
        user1.setUserRealName("王小柱");
        user1.setUserPassword("12344");
        user1.setRole(0);
        user1.setSchoolId(2);
        user1.setEmail("yyyyyyy@qq.com");
        user1.setAvatarUrl("");

        userMapper.insert(user);
        userMapper.insert(user1);
    }

    /**
     * 向数据库插入知识点数据
     * @author: ht
     */
    @Test
    public void knowledgePointsInsert() throws IOException {
        String path = "resources/xkw_knowledge_tree.json";

        ObjectMapper mapper = new ObjectMapper();

        List<KnowledgePoint> points = mapper.readValue(
                new File(path),
                new TypeReference<List<KnowledgePoint>>() {}
        );

        // 批量插入（逐条 insert 可使用批处理优化）
        for (KnowledgePoint point : points) {
            knowledgePointMapper.insert(point);
        }

        System.out.println("导入成功，共导入 " + points.size() + " 条知识点。");
    }

    @Test
    public void knowledgePointCountTest(){
        Long l = knowledgePointMapper.selectCount(null);
        System.out.println(l);
    }

    @Test
    public void knowledgePointSelectOneTest(){
        QueryWrapper<KnowledgePoint> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("name", "正负数的定义");
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectOne(objectQueryWrapper);
        System.out.println(knowledgePoint);
    }

    @Test
    public void userSelectTest(){
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }

    @Test
    public void selectQuestionByKnowledgePointTest(){
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
}
