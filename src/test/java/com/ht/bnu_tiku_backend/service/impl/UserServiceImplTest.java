package com.ht.bnu_tiku_backend.service.impl;

import com.ht.bnu_tiku_backend.service.UserService;
import com.ht.bnu_tiku_backend.utils.DTO.LoginResultDTO;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.request.UserLoginRequest;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Resource
    private UserService userService;

    @Test
    public void login() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserName("00001");
        userLoginRequest.setUserPassword("123456");
        Result<LoginResultDTO> login = userService.login(userLoginRequest);
        System.out.println(login);
    }

    @Test
    public void getCurrentUser() {
    }
}