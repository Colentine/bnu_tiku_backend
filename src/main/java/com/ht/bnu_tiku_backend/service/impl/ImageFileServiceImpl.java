package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.ImageFile;
import com.ht.bnu_tiku_backend.service.ImageFileService;
import com.ht.bnu_tiku_backend.mapper.ImageFileMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【image_file(图像表)】的数据库操作Service实现
* @createDate 2025-04-14 22:19:41
*/
@Service
public class ImageFileServiceImpl extends ServiceImpl<ImageFileMapper, ImageFile>
    implements ImageFileService{

}




