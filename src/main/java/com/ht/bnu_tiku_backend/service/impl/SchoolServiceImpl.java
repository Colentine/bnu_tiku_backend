package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.School;
import com.ht.bnu_tiku_backend.service.SchoolService;
import com.ht.bnu_tiku_backend.mapper.SchoolMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【school(学校表)】的数据库操作Service实现
* @createDate 2025-04-14 22:19:42
*/
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School>
    implements SchoolService{

}




