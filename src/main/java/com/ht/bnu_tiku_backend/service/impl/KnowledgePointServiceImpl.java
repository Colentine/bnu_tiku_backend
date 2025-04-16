package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.KnowledgePoint;
import com.ht.bnu_tiku_backend.service.KnowledgePointService;
import com.ht.bnu_tiku_backend.mapper.KnowledgePointMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【knowledge_point(知识点)】的数据库操作Service实现
* @createDate 2025-04-16 15:52:57
*/
@Service
public class KnowledgePointServiceImpl extends ServiceImpl<KnowledgePointMapper, KnowledgePoint>
    implements KnowledgePointService{

}




