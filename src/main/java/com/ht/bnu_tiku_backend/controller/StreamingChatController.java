package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.app.TikuApp;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
public class StreamingChatController {
    @Resource
    private TikuApp tikuApp;

    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doStreamChat(@RequestParam String message) {
        return tikuApp.doStreamChat(message).concatWith(Mono.just("[END]"));// ⚠️ 保证这个就是 Flux<String>
    }
}
