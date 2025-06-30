package com.ht.bnu_tiku_backend.elasticsearch.repository;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.List;

public interface EsComplexityTypeRepository extends ElasticsearchRepository<ComplexityType, Long> {
    List<ComplexityType> findComplexityTypesByIdIsIn(List<Long> ids);
}
