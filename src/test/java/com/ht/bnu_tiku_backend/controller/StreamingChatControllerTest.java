package com.ht.bnu_tiku_backend.controller;

import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
public class StreamingChatControllerTest {
    @Resource
    private WebTestClient webTestClient;

    @Test
    public void doStreamChat() {
        String inputMessage = "What is Pythagorean Theorem?";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/chat/stream-chat")
                        .queryParam("message", inputMessage)
                        .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .thenConsumeWhile(chunk -> {
                    System.out.println("🚀 流输出片段: " + chunk);
                    return true; // 会一直消费直到流结束
                })
                .verifyComplete();
    }
}