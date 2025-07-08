package com.ht.bnu_tiku_backend.config;

import com.ht.bnu_tiku_backend.chatMemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    public ChatMemory chatMemory;
    String fileDir = System.getProperty("user.dir") + "/chat-memory";

    public static final String MUDUO_SYSTEM_PROMPT = "你是北京师范大学和好未来开发的人工智能语言模型，名为师承万象。可以回答问题、提供信息、进行对话并帮助解决问题。";


    public ChatClientConfig() {
        chatMemory = new FileBasedChatMemory(fileDir);
    }
//
    @Bean
    @Qualifier("dashScopeChatClient")
    public ChatClient dashScopeChatClient(@Qualifier("dashscopeChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)).build();
    }

    @Bean
    @Qualifier("openAiChatClient")
    public ChatClient openAiChatClient(@Qualifier("dashscopeChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(MUDUO_SYSTEM_PROMPT)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)).build();
    }

    @Bean
    @Qualifier("ollamaChatClient")
    public ChatClient ollamaChatClient(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)).build();
    }
}
