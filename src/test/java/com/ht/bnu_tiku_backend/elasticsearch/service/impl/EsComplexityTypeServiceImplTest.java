package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import com.ht.bnu_tiku_backend.service.ComplexityTypeService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsComplexityTypeServiceImplTest {
    @Resource
    private EsComplexityTypeServiceImpl esComplexityTypeService;
    @Resource
    private ComplexityTypeService complexityTypeService;

    @Test
    public void insert() {
        complexityTypeService.list().forEach(mysqlComplexityType -> {
            ComplexityType complexityType = new ComplexityType();
            BeanUtils.copyProperties(mysqlComplexityType, complexityType);
            esComplexityTypeService.insert(complexityType);
        });
    }
}