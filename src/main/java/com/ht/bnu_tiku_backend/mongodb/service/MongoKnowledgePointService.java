package com.ht.bnu_tiku_backend.mongodb.service;

import com.ht.bnu_tiku_backend.mongodb.model.KnowledgePoint;

import java.util.List;

public interface MongoKnowledgePointService {
    void insertKnowledgePoint(KnowledgePoint knowledgePoint);

    List<KnowledgePoint> autoCompleteSearch(String query);

    List<KnowledgePoint> findAll();

    List<KnowledgePoint> updateKnowledge(KnowledgePoint knowledgePoint);
}
