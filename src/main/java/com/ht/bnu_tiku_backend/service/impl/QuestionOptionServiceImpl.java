package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.QuestionOption;
import com.ht.bnu_tiku_backend.service.QuestionOptionService;
import com.ht.bnu_tiku_backend.mapper.QuestionOptionMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【question_option(选择题选项表)】的数据库操作Service实现
* @createDate 2025-04-16 15:52:57
*/
@Service
public class QuestionOptionServiceImpl extends ServiceImpl<QuestionOptionMapper, QuestionOption>
    implements QuestionOptionService{

}




