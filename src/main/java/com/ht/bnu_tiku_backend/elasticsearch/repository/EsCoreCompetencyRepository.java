package com.ht.bnu_tiku_backend.elasticsearch.repository;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import com.ht.bnu_tiku_backend.elasticsearch.model.CoreCompetency;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsCoreCompetencyRepository extends ElasticsearchRepository<CoreCompetency, Long> {
    List<CoreCompetency> findCoreCompetenciesByIdIsIn(List<Long> ids);
}
