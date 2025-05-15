package com.ht.bnu_tiku_backend.config;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TikuAppVectorStoreConfig {
    @Resource
    private EmbeddingModel dashScopeEmbeddingModel;

    @Bean
    VectorStore tikuAppVectorStore() {
        return null;
    }
}