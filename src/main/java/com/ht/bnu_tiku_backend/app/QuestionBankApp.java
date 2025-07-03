package com.ht.bnu_tiku_backend.app;
import com.ht.bnu_tiku_backend.config.ChatClientConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;


@Component
@Slf4j
public class QuestionBankApp {

    @Resource
    @Qualifier("dashScopeChatClient")
    private ChatClient dashScopeChatClient;
    @Resource
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;
    @Resource
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;
    @Resource
    private ChatClientConfig chatClientConfig;

    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = openAiChatClient
                .prompt()
                .user(message)
                .advisors(spec-> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;
    }

    public Flux<String> doStreamChat(String message, String chatId, String modelId) {

        ChatClient chat = switch (modelId) {
            case "MuduoLLM" -> openAiChatClient;
            case "gemma3"   -> ollamaChatClient;
            default         -> dashScopeChatClient;
        };

        // 用来累积整段回答
        StringBuilder buf = new StringBuilder();

        return chat.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()                       // Flux<ChatResponse>
                .content()                      // Flux<String> (delta tokens)
                .doOnNext(buf::append)          // ★ 1. 边流边拼
                .doFinally(sig ->               // ★ 2. 完结 / 取消 / 报错 都会触发
                        saveAndStop(chatId, buf.toString())
                );
    }

    private void saveAndStop(String sid, String fullText) {
        // 停掉下游 LLM，如果你的 ChatClient 支持
        // someChatClient.stop(sid);
        // 把完整回答写入对话内存 / DB / 文件
        chatClientConfig.chatMemory.add(sid, new AssistantMessage(fullText));
    }
//    public String doChatWithRag(String message) {
//        ChatResponse chatResponse = chatClient.prompt()
//                .user(message)
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .call()
//                .chatResponse();
//        assert chatResponse != null;
//        String text = chatResponse.getResult().getOutput().getText();
//        log.info("text:{}", text);
//        return text;
//    }
}
