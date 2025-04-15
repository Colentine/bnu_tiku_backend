package com.ht.bnu_tiku_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.model.domain.User;
import com.ht.bnu_tiku_backend.service.UserService;
import com.ht.bnu_tiku_backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author huangtao
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-04-14 22:19:42
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




