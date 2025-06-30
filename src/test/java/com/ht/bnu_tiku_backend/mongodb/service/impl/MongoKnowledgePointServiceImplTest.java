package com.ht.bnu_tiku_backend.mongodb.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.mongodb.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.utils.PinYinTool;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoKnowledgePointServiceImplTest {
    @Resource
    private MongoKnowledgePointServiceImpl mongoKnowledgePointService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void insertKnowledgePoint() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String path = "KnowledgeTree/xkb_knowledge_tree.json";

        objectMapper.readValue(new File(path), new TypeReference<List<KnowledgePoint>>() {
        }).forEach(knowledgePoint -> {
            mongoKnowledgePointService.insertKnowledgePoint(knowledgePoint);
        });
    }

    @Test
    public void autoCompleteSearch() {
        List<KnowledgePoint> knowledgePoints = mongoKnowledgePointService.autoCompleteSearch("s");
        System.out.println(knowledgePoints);
    }

    @Test
    public void transferKnowledgeNameToPinyin() {
        mongoKnowledgePointService.findAll().forEach(knowledgePoint -> {
            System.out.println(PinYinTool.getInitials(knowledgePoint.getName()));
            Query query = new Query(Criteria.where("_id").is(knowledgePoint.getId()));
            Update update = new Update().set("name_initials", PinYinTool.getInitials(knowledgePoint.getName()));
            mongoTemplate.updateFirst(query, update, KnowledgePoint.class);
        });
    }

//    @Test
//    public void getName(){
//        System.out.println(mongoConfig.getDatabaseName1());
//    }

}