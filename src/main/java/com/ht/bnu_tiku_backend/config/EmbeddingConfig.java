package com.ht.bnu_tiku_backend.config;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingConfig {
  @Resource
  DashScopeEmbeddingModel dashScopeEmbeddingModel;
  // 把想给 ES 用的那颗标记成 Primary
  @Bean
  @Primary
  public EmbeddingModel embeddingModel() {
      return dashScopeEmbeddingModel;     // 就是把 openAiEmbeddingModel 重新暴露为 “唯一默认”
  }
}
