package com.ht.bnu_tiku_backend.mongodb.repository;

import com.ht.bnu_tiku_backend.mongodb.model.KnowledgePoint;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KnowledgePointRepository extends MongoRepository<KnowledgePoint, String> {
}
