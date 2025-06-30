package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import com.ht.bnu_tiku_backend.elasticsearch.model.CoreCompetency;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsCoreCompetencyService;
import com.ht.bnu_tiku_backend.service.CoreCompetencyService;
import com.ht.bnu_tiku_backend.service.impl.CoreCompetencyServiceImpl;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsCoreCompetencyServiceImplTest {
    @Resource
    private EsCoreCompetencyService esCoreCompetencyService;
    @Resource
    private CoreCompetencyService coreCompetencyService;

    @Test
    public void insert() {
        coreCompetencyService.list().forEach(mysqlComplexityType -> {
            CoreCompetency coreCompetency = new CoreCompetency();
            BeanUtils.copyProperties(mysqlComplexityType, coreCompetency);
            esCoreCompetencyService.insert(coreCompetency);
        });
    }
}