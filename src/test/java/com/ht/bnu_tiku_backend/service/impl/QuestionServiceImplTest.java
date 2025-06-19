package com.ht.bnu_tiku_backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.service.impl.QuestionServiceImpl;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionServiceImplTest {
    @Resource
    private QuestionServiceImpl questionService;

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
    }

    @Test
    public void pageQueryQuestionsByKnowledgePoint() {
    }
}