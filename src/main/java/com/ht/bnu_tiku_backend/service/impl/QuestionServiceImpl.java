package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.Question;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【question(习题表)】的数据库操作Service实现
* @createDate 2025-04-14 22:19:42
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




