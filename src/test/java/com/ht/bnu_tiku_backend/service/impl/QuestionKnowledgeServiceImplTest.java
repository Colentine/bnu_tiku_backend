package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.bnu_tiku_backend.mapper.QuestionKnowledgeMapper;
import com.ht.bnu_tiku_backend.model.domain.QuestionKnowledge;
import com.ht.bnu_tiku_backend.service.QuestionKnowledgeService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionKnowledgeServiceImplTest {
    @Resource
    private QuestionKnowledgeMapper questionKnowledgeMapper;

    @Test
    public void test() {
        QueryWrapper<QuestionKnowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("question_id", List.of(27L, 28L, 29L, 37L, 39L,40L, 41L, 45L, 47L, 49L, 118L, 190L, 219L, 247L, 337L, 354L, 869L, 994L));
        questionKnowledgeMapper.delete(queryWrapper);
    }
}