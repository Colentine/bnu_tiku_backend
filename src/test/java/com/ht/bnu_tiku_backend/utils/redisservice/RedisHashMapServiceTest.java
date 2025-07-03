package com.ht.bnu_tiku_backend.utils.redisservice;

import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisHashMapServiceTest {
    @Resource
    private RedisHashMapService redisHashMapService;

    @Test
    public void set() {
        redisHashMapService.set("2", "3", "1");
    }

    @Test
    public void getOne() {
    }

    @Test
    public void getEntries() {
    }

    @Test
    public void deleteOne() {
        redisHashMapService.deleteAll("2");
    }

    @Test
    public void deleteAll() {
    }

    @Test
    public void exists() {
    }
}