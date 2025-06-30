package com.ht.bnu_tiku_backend.utils.redisservice;

import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisServiceTest {
    @Resource
    private RedisService redisService;

    @Test
    public void set() {
        redisService.set("ht", "redis hello~~", 1000, TimeUnit.MINUTES);
    }

    @Test
    public void get() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void exists() {
    }
}