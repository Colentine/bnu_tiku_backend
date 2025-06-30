package com.ht.bnu_tiku_backend.app;

import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionBankAppTest {
    @Resource
    private QuestionBankApp questionBankApp;

    @Resource
    @Qualifier("dashscopeChatModel")
    private ChatModel dashscopeChatModel;

    @Test
    public void doChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小h，你是什么模型？";
        questionBankApp.doChat(message, chatId);
        message = "很棒";
        questionBankApp.doChat(message, chatId);
        message = "刚才，小h问的问题是什么？";
        questionBankApp.doChat(message, chatId);
//        dashscopeChatModel.call(message);
    }

//    @Test
//    public void doChatWithRag() {
//        String uuid = UUID.randomUUID().toString();
//        String s = questionBankApp.doChatWithRag("推荐一道考察有理数混合运算的应用，有理数大小比较吧");
//        System.out.println(s);
//    }

    @Test
    public void doStreamChat() throws InterruptedException {
        String chatId = UUID.randomUUID().toString();
        questionBankApp.doStreamChat("你好，我是小h，你是什么模型？", chatId, "MuduoLLM")
                .flatMap(piece -> Flux.fromStream(piece.chars().mapToObj(c -> String.valueOf((char) c))))
                .delayElements(Duration.ofMillis(500))
                .subscribe(c -> {
                    System.out.print(c);
                    System.out.flush(); // ✅ 每次强制刷新
                });
//
//        questionBankApp.doStreamChat("很棒", chatId)
//                .flatMap(piece -> Flux.fromStream(piece.chars().mapToObj(c -> String.valueOf((char) c))))
//                .delayElements(Duration.ofMillis(500))
//                .subscribe(c -> {
//                    System.out.print(c);
//                    System.out.flush(); // ✅ 每次强制刷新
//                });
//
//        questionBankApp.doStreamChat("刚才，小h问的问题是什么？", chatId)
//                .flatMap(piece -> Flux.fromStream(piece.chars().mapToObj(c -> String.valueOf((char) c))))
//                .delayElements(Duration.ofMillis(500))
//                .subscribe(c -> {
//                    System.out.print(c);
//                    System.out.flush(); // ✅ 每次强制刷新
//                });

        Thread.sleep(30000);
    }
}