package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.service.UserService;
import com.ht.bnu_tiku_backend.utils.DTO.LoginResultDTO;
import com.ht.bnu_tiku_backend.utils.DTO.UserDTO;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.UserHolder;
import com.ht.bnu_tiku_backend.utils.request.UserLoginRequest;
import com.ht.bnu_tiku_backend.utils.request.UserRegisterRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public Result<LoginResultDTO> login(@RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(userLoginRequest);
    }

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

    /**
     * 获取用户登录态
     * @return
     */
    @GetMapping("/current")
    public Result<UserDTO> currentUser() {
        return Result.ok(UserHolder.getUser());
    }
}
