package com.ht.bnu_tiku_backend.utils.redisservice;

import com.ht.bnu_tiku_backend.controller.StreamingChatController;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisListServiceTest {
    @Resource
    private RedisListService redisListService;
    @Resource
    private StreamingChatController streamingChatController;

    @Test
    public void deleteOne() {
        streamingChatController.doDeleteOneConversation("2", "1");
    }
}