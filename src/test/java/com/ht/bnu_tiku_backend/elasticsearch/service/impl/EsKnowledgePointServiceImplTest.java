package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.utils.PinYinTool;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsKnowledgePointServiceImplTest {
    @Resource
    private EsKnowledgePointServiceImpl esKnowledgePointService;

    @Test
    public void saveKnowledgePoint() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String path = "KnowledgeTree/xkb_knowledge_tree.json";
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        objectMapper.readValue(is, new TypeReference<List<KnowledgePoint>>() {
        }).forEach(knowledgePoint -> {
            knowledgePoint.setNameInitials(PinYinTool.getInitials(knowledgePoint.getName()));
            esKnowledgePointService.saveKnowledgePoint(knowledgePoint);
        });
    }


    @Test
    public void autoCompleteSearch() {
//        List<com.ht.bnu_tiku_backend.mongodb.model.KnowledgePoint> knowledgePoints = mongoKnowledgePointService.autoCompleteSearch("s");
//        System.out.println(knowledgePoints);
    }

    @Test
    public void transferKnowledgeNameToPinyin() {
    }

    @Test
    public void testSaveKnowledgePoint() {
    }

    @Test
    public void testAutoCompleteSearch() {
        System.out.println(esKnowledgePointService.autoCompleteSearch("yls"));
    }
}