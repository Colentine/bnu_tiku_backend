package com.ht.bnu_tiku_backend.mongodb.service.impl;

import com.ht.bnu_tiku_backend.mongodb.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.mongodb.repository.KnowledgePointRepository;
import com.ht.bnu_tiku_backend.mongodb.service.MongoKnowledgePointService;
import com.ht.bnu_tiku_backend.utils.PinYinTool;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoKnowledgePointServiceImpl implements MongoKnowledgePointService {
    @Resource
    KnowledgePointRepository knowledgePointRepository;

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public void insertKnowledgePoint(KnowledgePoint knowledgePoint) {
        knowledgePointRepository.save(knowledgePoint);
    }

    @Override
    public List<KnowledgePoint> autoCompleteSearch(String query) {
        Document searchStage = new Document("$search", new Document()
                .append("index", "knowledge_autocomplete")
                .append("compound", new Document()
                        .append("should", List.of(
                                new Document("text", new Document()
                                        .append("query", query)
                                        .append("path", "name")
                                ),
                                new Document("autocomplete", new Document()
                                        .append("query", query)
                                        .append("path", "name_initials")
                                )
                        ))
                )
        );

        Aggregation aggregation = Aggregation.newAggregation(
                context -> searchStage,
                Aggregation.limit(40)
        );

        AggregationResults<KnowledgePoint> results = mongoTemplate.aggregate(
                aggregation,
                "knowledge_point",
                KnowledgePoint.class
        );

        return results.getMappedResults();
    }

    @Override
    public List<KnowledgePoint> findAll() {
        return knowledgePointRepository.findAll();
    }

    @Override
    public List<KnowledgePoint> updateKnowledge(KnowledgePoint knowledgePoint) {
        return null;
    }
}
