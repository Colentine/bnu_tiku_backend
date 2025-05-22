package com.ht.bnu_tiku_backend.rag.document.readers;

import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsQuestionServiceImpl;
import com.ht.bnu_tiku_backend.rag.document.loaders.EsVectorStoreLoader;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionDocumentReaderTest {
    @Resource
    EsQuestionServiceImpl esQuestionService;
    @Autowired
    private EsVectorStoreLoader esVectorStoreLoader;

    @Test
    public void fromQuestion() {
        PageQueryQuestionResult pageQueryQuestionResult = esQuestionService
                .queryQuestionsByKnowledgePointNames(Collections.singletonList("beforeMount"),
                        1L, 20L);

        System.out.println(pageQueryQuestionResult);

        List<Document> documentList = pageQueryQuestionResult.getQuestions()
                .stream()
                .map(QuestionDocumentReader::fromQuestion)
                .toList();

        esVectorStoreLoader.storeDocuments(documentList);
    }

    @Test
    public void buildSimpleQuestionDocument() {
    }

    @Test
    public void buildCompositeQuestionDocument() {
    }

    @Test
    public void transformDifficultyToTag() {
    }

    @Test
    public void transformQuestionTypeToTag() {
    }
}