package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.app.QuestionBankApp;
import com.ht.bnu_tiku_backend.config.ChatClientConfig;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.redisservice.RedisHashMapService;
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
    @Resource
    private RedisHashMapService redisHashMapService;

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

    @PostMapping("/add/conversation/{userId}/{chatId}/{chatName}")
    public Result<Boolean> doAddConversation(
            @PathVariable(value = "chatId") String chatId,
            @PathVariable(value = "userId") String userId,
            @PathVariable(value = "chatName") String chatName
    ) {
        log.info("chatId={}, userId={}, chatName={}", chatId, userId, chatName);
//        redisListService.set(userId, chatId);
        try {
            redisHashMapService.set(userId, chatId, chatName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.ok(true);
    }

    @GetMapping("/get/conversation/ids/{userId}")
    public Result<List<String>> doGetConversationIds(
            @PathVariable(value = "userId") String userId
    ) {
        log.info("userId={}", userId);
        List<String> ids = null;
        try {
            ids = redisHashMapService.getEntries(userId).keySet().stream().map(o -> (String) o).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.ok(ids);
    }

    @GetMapping("/get/conversation/names/{userId}")
    public Result<List<String>> doGetConversationNames(
            @PathVariable(value = "userId") String userId
    ) {
        log.info("userId={}", userId);
        List<String> names = null;
        try {
            names = redisHashMapService.getEntries(userId).values().stream().map(o -> (String) o).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.ok(names);
    }

    @PostMapping("/delete/conversation/{userId}/{chatId}")
    public Result<Boolean> doDeleteOneConversation(
            @PathVariable(value = "userId") String userId,
            @PathVariable(value = "chatId") String chatId
    ) {
        log.info("userId={}, chatId={}", userId, chatId);
        Long deleteNumber = null;
        try {
            deleteNumber = redisHashMapService.deleteOne(userId, chatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("deleteNumber={}", deleteNumber);
        return Result.ok(true);
    }

    @PostMapping("/delete/all/conversation/{userId}")
    public Result<Boolean> doDeleteAllConversation(
            @PathVariable(value = "userId") String userId
    ) {
        log.info("userId={}", userId);
        try {
            redisHashMapService.deleteAll(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.ok(true);
    }
}
