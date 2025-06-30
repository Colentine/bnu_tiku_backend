package com.ht.bnu_tiku_backend.elasticsearch.repository;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import com.ht.bnu_tiku_backend.elasticsearch.model.Source;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.List;

public interface EsSourceRepository extends ElasticsearchRepository<Source, Long> {
    List<Source> findSourcesByIdIsIn(List<Long> ids);
}
