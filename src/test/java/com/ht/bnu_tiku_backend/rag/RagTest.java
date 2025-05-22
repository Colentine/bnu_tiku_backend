package com.ht.bnu_tiku_backend.rag;

import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsQuestionServiceImpl;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.autoconfigure.vectorstore.elasticsearch.ElasticsearchVectorStoreProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RagTest {
    @Resource
    VectorStore vectorStore;

    @Resource
    EsQuestionServiceImpl esQuestionServiceImpl;

    @Test
    public void esVectorStoreTest() throws IOException {

//        List<Document> documents = List.of(
//                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
//                new Document("The World is Big and Salvation Lurks Around the Corner"),
//                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
//
//        vectorStore.add(documents);

//        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
//        System.out.println(results);
        System.out.println(esQuestionServiceImpl.queryQuestionsByKnowledgePointNames(Collections.singletonList("beforeMount"), 1L, 10L));
    }
}
