package com.ht.bnu_tiku_backend.elasticsearch.repository;

import com.ht.bnu_tiku_backend.elasticsearch.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsQuestionRepository extends ElasticsearchRepository<Question, Long> {
    List<Question> findByKnowledgePointIdsIn(List<Long> knowledgePointId);

    Page<Question> findByKnowledgePointIdsIn(List<Long> knowledgePointId, Pageable pageable);

    List<Question> findByParentId(Long parentId);

    Question findByQuestionId(Integer parentId);

    List<Question> findByQuestionIdIn(List<Long> questionIds);
}
