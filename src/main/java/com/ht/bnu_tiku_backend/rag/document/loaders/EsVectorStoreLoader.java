package com.ht.bnu_tiku_backend.rag.document.loaders;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EsVectorStoreLoader {
    @Resource
    private VectorStore vectorStore;

    public void storeDocuments(List<Document> documents) {
        vectorStore.accept(documents);
    }
}

