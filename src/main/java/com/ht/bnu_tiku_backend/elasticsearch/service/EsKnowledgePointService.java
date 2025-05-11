package com.ht.bnu_tiku_backend.elasticsearch.service;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;

public interface EsKnowledgePointService {
    void saveKnowledgePoint(KnowledgePoint knowledgePoint);
}
