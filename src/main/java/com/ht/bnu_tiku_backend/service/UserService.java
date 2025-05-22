package com.ht.bnu_tiku_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.bnu_tiku_backend.model.domain.User;
import com.ht.bnu_tiku_backend.utils.DTO.LoginResultDTO;
import com.ht.bnu_tiku_backend.utils.DTO.UserDTO;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.request.UserLoginRequest;
import com.ht.bnu_tiku_backend.utils.request.UserRegisterRequest;

/**
* @author huangtao
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-04-17 15:23:47
*/
public interface UserService extends IService<User> {

    Result<LoginResultDTO> login(UserLoginRequest userLoginRequest);

    Result<UserDTO> register(UserRegisterRequest userRegisterRequest);
}
