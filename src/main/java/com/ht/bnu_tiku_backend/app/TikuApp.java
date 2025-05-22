package com.ht.bnu_tiku_backend.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class TikuApp {
    private final ChatClient chatClient;

    public TikuApp(ChatModel dashScopeChatModel) {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public static final String SYSTEM_PROMPT = "你是一位智慧的数学教学助手。";

    @Resource
    private VectorStore vectorStore;

    public String doChat(String message) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }

    public Flux<String> doStreamChat(String message) {
        return chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }

    public String doChatWithRag(String message) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .chatResponse();
        assert chatResponse != null;
        String text = chatResponse.getResult().getOutput().getText();
        log.info("text:{}",text);
        return text;
    }
}
