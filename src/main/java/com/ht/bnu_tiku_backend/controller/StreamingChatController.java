package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.app.QuestionBankApp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/chat")
public class StreamingChatController {
    @Resource
    private QuestionBankApp questionBankApp;

    /**
     * LLM流式响应
     * @param message
     * @return
     */
    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doStreamChat(@RequestParam String message) {
        log.info("用户message={}", message);
        return questionBankApp.doStreamChat(message).concatWith(Mono.just("[END]"));// ⚠️ 保证这个就是 Flux<String>
    }
}
