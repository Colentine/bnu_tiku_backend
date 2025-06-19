package com.ht.bnu_tiku_backend.mongodb.repository;

import org.springframework.data.domain.Page;
import com.ht.bnu_tiku_backend.mongodb.model.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByKnowledgePointIdsIn(List<Long> knowledgePointId);

    Page<Question> findByKnowledgePointIdsIn(List<Long> knowledgePointId, Pageable pageable);

    List<Question> findByParentId(Long parentId);
}