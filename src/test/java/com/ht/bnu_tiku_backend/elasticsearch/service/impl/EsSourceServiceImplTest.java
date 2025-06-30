package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.Source;
import com.ht.bnu_tiku_backend.service.SourceService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsSourceServiceImplTest {
    @Resource
    private EsSourceServiceImpl esSourceServiceImpl;
    @Resource
    private SourceService sourceService;

    @Test
    public void insert() {
        sourceService.list().forEach(mysqlSource -> {
            Source source = new Source();
            BeanUtils.copyProperties(mysqlSource, source);
            esSourceServiceImpl.insert(source);
        });
    }
}