package com.ht.bnu_tiku_backend.elasticsearch.repository;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import com.ht.bnu_tiku_backend.elasticsearch.model.Grade;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.List;

public interface EsGradeRepository extends ElasticsearchRepository<Grade, Long> {
    List<Grade> findGradesByIdIsIn(List<Integer> ids);
}
