package com.ht.bnu_tiku_backend.elasticsearch.repository;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsKnowledgePointRepository extends ElasticsearchRepository<KnowledgePoint, Long> {
}
