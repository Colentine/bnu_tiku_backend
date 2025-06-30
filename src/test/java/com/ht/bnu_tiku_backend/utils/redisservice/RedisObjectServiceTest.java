package com.ht.bnu_tiku_backend.utils.redisservice;

import com.ht.bnu_tiku_backend.model.domain.Question;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisObjectServiceTest {
    @Resource
    private RedisObjectService redisObjectService;

//    @Resource
//    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void set() {
        Question question = new Question();
        question.setId(0L);
        question.setParentId(0L);
        question.setQuestionType(0);
        question.setSimpleQuestionType(0);
        question.setSubject("");
        question.setGradeId(0L);
        question.setSourceId(0L);
        question.setDifficulty(0.0D);
        question.setComplexityTypeId(0L);
        question.setCoreCompetencyId(0L);
        question.setCreatedBy(0L);
        question.setCreatedAt(new Date());
        question.setUpdatedAt(new Date());

        redisObjectService.set("sample-question~", question, 1000, TimeUnit.MINUTES);
    }

    @Test
    public void get() {
        Question question = redisObjectService.get("sample-question~", Question.class);
        System.out.println(question);
    }

    @Test
    public void delete() {
    }
}