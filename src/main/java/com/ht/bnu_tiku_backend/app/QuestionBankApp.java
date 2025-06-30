package com.ht.bnu_tiku_backend.app;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;


@Component
@Slf4j
public class QuestionBankApp {
//    @Resource
//    private ChatClient dashscopeChatClient;
//    @Resource
//    private ChatClient openAiChatClient;
    @Resource
    @Qualifier("dashScopeChatClient")
    private ChatClient dashScopeChatClient;
    @Resource
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;
    @Resource
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;

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
        ChatClient chatClient;
        if (Objects.equals(modelId, "MuduoLLM")){
            log.info("MuduoLLM已加载");
            chatClient = openAiChatClient;
        }
        else if (Objects.equals(modelId, "gemma3")) {
            log.info("Gemma3已加载");
            chatClient = ollamaChatClient;
        }
        else{
            log.info("Qwen已加载");
            chatClient = dashScopeChatClient;
        }

        return chatClient
                .prompt()
                .user(message)
                .advisors(spec-> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
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
