package com.ht.bnu_tiku_backend.app;

import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionBankAppTest {
    @Resource
    private QuestionBankApp questionBankApp;

    @Test
    public void doChat() {
        questionBankApp.doChat("你好啊,你是什么模型?");
    }

    @Test
    public void doChatWithRag() {
        String uuid = UUID.randomUUID().toString();
        String s = questionBankApp.doChatWithRag("推荐一道考察有理数混合运算的应用，有理数大小比较吧");
        System.out.println(s);
    }

    @Test
    public void doStreamChat() throws InterruptedException {
        questionBankApp.doStreamChat("你好呀")
                .flatMap(piece -> Flux.fromStream(piece.chars().mapToObj(c -> String.valueOf((char) c))))
                .delayElements(Duration.ofMillis(500))
                .subscribe(c -> {
                    System.out.print(c);
                    System.out.flush(); // ✅ 每次强制刷新
                });

        Thread.sleep(30000);
    }
}