package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.QuestionKnowledge;
import com.ht.bnu_tiku_backend.service.QuestionKnowledgeService;
import com.ht.bnu_tiku_backend.mapper.QuestionKnowledgeMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【question_knowledge(习题知识点关联表)】的数据库操作Service实现
* @createDate 2025-04-17 15:23:47
*/
@Service
public class QuestionKnowledgeServiceImpl extends ServiceImpl<QuestionKnowledgeMapper, QuestionKnowledge>
    implements QuestionKnowledgeService{

}




