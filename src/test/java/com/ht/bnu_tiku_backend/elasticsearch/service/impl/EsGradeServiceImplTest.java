package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.Grade;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsGradeService;
import com.ht.bnu_tiku_backend.service.GradeService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EsGradeServiceImplTest {
    @Resource
    private EsGradeServiceImpl esGradeService;
    @Resource
    private GradeService gradeService;

    @Test
    public void insert() {
        gradeService.list().forEach(mysqlGrade->{
            Grade grade = new Grade();
            BeanUtils.copyProperties(mysqlGrade,grade);
            esGradeService.insert(grade);
        });
    }
}