package com.ht.bnu_tiku_backend.model.mongodb.service;


import com.ht.bnu_tiku_backend.mongodb.model.User;
import com.ht.bnu_tiku_backend.mongodb.service.impl.MongoUserServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class MongoUserServiceTest {
    @Resource
    private MongoUserServiceImpl mongoUserService;


    @Test
    public void insertUserTest(){
        User user = new User();
        user.setId("123458111212123");
        user.setName("");
        user.setAge(0);
        user.setEmail("");

        mongoUserService.saveUser(user);
    }
}