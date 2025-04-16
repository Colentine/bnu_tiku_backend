package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.Grade;
import com.ht.bnu_tiku_backend.service.GradeService;
import com.ht.bnu_tiku_backend.mapper.GradeMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【grade(年级)】的数据库操作Service实现
* @createDate 2025-04-16 15:52:57
*/
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade>
    implements GradeService{

}




