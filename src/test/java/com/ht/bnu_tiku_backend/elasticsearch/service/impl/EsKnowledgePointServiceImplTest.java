package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsKnowledgePointServiceImplTest {
    @Resource
    private EsKnowledgePointServiceImpl esKnowledgePointService;

    @Test
    public void saveKnowledgePoint() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String path = "resources/KnowledgeTree/xkb_knowledge_tree.json";

        objectMapper.readValue(new File(path), new TypeReference<List<KnowledgePoint>>() {
        }).forEach(knowledgePoint -> {
            esKnowledgePointService.saveKnowledgePoint(knowledgePoint);
        });
    }
}