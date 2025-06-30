package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.Source;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsSourceRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsSourceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EsSourceServiceImpl implements EsSourceService {
    @Resource
    EsSourceRepository esSourceRepository;

    @Override
    public void insert(Source source) {
        esSourceRepository.save(source);
    }
}
