package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.Source;
import com.ht.bnu_tiku_backend.service.SourceService;
import com.ht.bnu_tiku_backend.mapper.SourceMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【source(习题来源表)】的数据库操作Service实现
* @createDate 2025-04-14 22:19:42
*/
@Service
public class SourceServiceImpl extends ServiceImpl<SourceMapper, Source>
    implements SourceService{

}




