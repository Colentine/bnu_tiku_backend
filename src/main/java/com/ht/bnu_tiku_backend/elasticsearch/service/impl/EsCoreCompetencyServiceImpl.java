package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import com.ht.bnu_tiku_backend.elasticsearch.model.CoreCompetency;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsCoreCompetencyRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsCoreCompetencyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EsCoreCompetencyServiceImpl implements EsCoreCompetencyService {
    @Resource
    private EsCoreCompetencyRepository esCoreCompetencyRepository;
    @Override

    public void insert(CoreCompetency coreCompetency) {
        esCoreCompetencyRepository.save(coreCompetency);
    }
}
