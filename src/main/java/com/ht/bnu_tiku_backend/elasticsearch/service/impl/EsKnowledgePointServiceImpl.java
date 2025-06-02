package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsKnowledgePointRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsKnowledgePointService;
import jakarta.annotation.Resource;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EsKnowledgePointServiceImpl implements EsKnowledgePointService {
    @Resource
    EsKnowledgePointRepository esKnowledgePointRepository;

    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void saveKnowledgePoint(KnowledgePoint knowledgePoint) {
        esKnowledgePointRepository.save(knowledgePoint);
    }

    @Override
    public List<KnowledgePoint> autoCompleteSearch(String name) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        // 中文名称匹配（分词）
                        .should(s -> s.match(m -> m
                                .field("name")
                                .query(name)))
                        // 首字母前缀，如 ecgs
                        .should(s -> s.prefix(p -> p
                                .field("name_initials")
                                .value(name.toLowerCase())))
                ))
                .build();
        SearchHits<KnowledgePoint> hits =
                elasticsearchTemplate.search(query, KnowledgePoint.class);

        return hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
