package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.app.QuestionBankApp;
import com.ht.bnu_tiku_backend.config.ChatClientConfig;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.redisservice.RedisListService;
import com.ht.bnu_tiku_backend.utils.redisservice.RedisObjectService;
import com.ht.bnu_tiku_backend.utils.redisservice.RedisService;
import com.ht.bnu_tiku_backend.utils.request.StreamChatRequestParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chat")
public class StreamingChatController {
    @Resource
    private QuestionBankApp questionBankApp;
    @Resource
    private ChatClientConfig chatClientConfig;
    @Resource
    private RedisService redisService;
    @Resource
    private RedisListService redisListService;

    /**
     * LLM流式响应
     *
     * @param chatId
     * @return
     */
    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doStreamChat(
            @RequestParam String message,
            @RequestParam String chatId,
            @RequestParam String modelId
    ) {
        log.info("message={}, chatId={}, modelId={} ", message, chatId, modelId);
        return questionBankApp.doStreamChat(message
                        , chatId
                        , modelId)
                .concatWith(Mono.just("[END]"));// ⚠️ 保证这个就是 Flux<String>
    }

    @GetMapping("/get/messages/{conversationId}")
    public Result<List<Message>> doGetMessages(@PathVariable(value = "conversationId") String conversationId) {
        log.info("conversationId={}", conversationId);
        List<Message> messages;
        try {
            messages = chatClientConfig.chatMemory.get(conversationId, 10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("messages={}", messages);
        return Result.ok(messages);
    }

    @GetMapping("/add/conversation/{chatId}/{userId}")
    public Result<Boolean> doAddConversation(
            @PathVariable(value = "chatId") String chatId,
            @PathVariable(value = "userId") String userId
    ) {
        log.info("chatId={}, userId={}", chatId, userId);
        redisListService.set(userId, chatId);
        return Result.ok(true);
    }

    @GetMapping("/get/conversation/{userId}")
    public Result<List<String>> doGetConversations(
            @PathVariable(value = "userId") String userId
    ) {
        log.info("userId={}", userId);
        return Result.ok(redisListService.get(userId));
    }
}
