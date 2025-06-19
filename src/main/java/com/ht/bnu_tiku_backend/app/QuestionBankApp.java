package com.ht.bnu_tiku_backend.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class QuestionBankApp {
    public static final String SYSTEM_PROMPT = "你是一位智慧的数学教学助手。";
    private final ChatClient chatClient;
    @Resource
    private VectorStore vectorStore;

    public QuestionBankApp(ChatModel dashScopeChatModel) {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public String doChat(String message) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
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
        log.info("text:{}", text);
        return text;
    }
}
