package com.ht.bnu_tiku_backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.mapper.QuestionMapper;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionServiceImplTest {
    @Resource
    private QuestionServiceImpl questionService;
    @Resource
    private QuestionMapper questionMapper;

    @Test
    public void queryQuestionsByKnowledgePointIds() throws JsonProcessingException {
        questionService.queryQuestionsByQuestionIds(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L));
    }

    @Test
    public void queryQuestionsByKnowledgePoint() {
    }

    @Test
    public void queryQuestionsBySource() {
    }

    @Test
    public void queryQuestionsByGrade() {
    }

    @Test
    public void queryQuestionsByDifficulty() {
    }

    @Test
    public void deleteQuestion() {
        questionMapper.deleteByIds(List.of(27L, 28L, 29L, 37L, 39L,40L, 41L, 45L, 47L, 49L, 118L, 190L, 219L, 247L, 337L, 354L, 869L, 994L));
    }

    @Test
    public void pageQueryQuestionsByKnowledgePoint() {
    }
}