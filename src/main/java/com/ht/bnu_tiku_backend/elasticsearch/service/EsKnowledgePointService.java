package com.ht.bnu_tiku_backend.elasticsearch.service;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;

import java.util.List;

public interface EsKnowledgePointService {
    void saveKnowledgePoint(KnowledgePoint knowledgePoint);

    List<KnowledgePoint> autoCompleteSearch(String name);
}
