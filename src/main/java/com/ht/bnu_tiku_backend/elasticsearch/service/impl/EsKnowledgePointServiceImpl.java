package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsKnowledgePointRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsKnowledgePointService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EsKnowledgePointServiceImpl implements EsKnowledgePointService {
    @Resource
    EsKnowledgePointRepository esKnowledgePointRepository;

    @Override
    public void saveKnowledgePoint(KnowledgePoint knowledgePoint) {
        esKnowledgePointRepository.save(knowledgePoint);
    }
}
