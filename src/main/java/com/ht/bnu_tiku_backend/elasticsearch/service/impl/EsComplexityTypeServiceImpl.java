package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.ComplexityType;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsComplexityTypeRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsComplexityTypeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EsComplexityTypeServiceImpl implements EsComplexityTypeService {
    @Resource
    private EsComplexityTypeRepository esComplexityTypeRepository;

    @Override
    public void insert(ComplexityType complexityType) {
        esComplexityTypeRepository.save(complexityType);
    }
}
