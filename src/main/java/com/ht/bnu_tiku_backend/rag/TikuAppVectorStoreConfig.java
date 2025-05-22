package com.ht.bnu_tiku_backend.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikuAppVectorStoreConfig {
    @Resource
    private EmbeddingModel dashScopeEmbeddingModel;

    @Bean
    VectorStore tikuAppVectorStore() {
        return null;
    }
}