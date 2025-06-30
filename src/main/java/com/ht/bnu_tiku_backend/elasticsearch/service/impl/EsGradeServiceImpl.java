package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.Grade;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsGradeRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsGradeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EsGradeServiceImpl implements EsGradeService {
    @Resource
    private EsGradeRepository esGradeRepository;

    @Override
    public void insert(Grade grade) {
        esGradeRepository.save(grade);
    }
}
