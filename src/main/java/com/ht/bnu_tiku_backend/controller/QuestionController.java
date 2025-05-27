package com.ht.bnu_tiku_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsQuestionServiceImpl;
import com.ht.bnu_tiku_backend.mongodb.service.MongoQuestionService;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question")
public class QuestionController {
    @Resource
    QuestionService questionService;

    @Resource
    MongoQuestionService mongoQuestionService;

    @Resource
    EsQuestionServiceImpl esQuestionService;

//    @GetMapping("/search/kp/{name}")
//    public List<Map<String, String>> searchQuestionByKnowledgePoint(@PathVariable(value = "name") String name)
//            throws JsonProcessingException {
//        System.out.println(name);
//        List<Map<String, String>> maps = questionService.queryQuestionsByKnowledgePoint(name);
//        System.out.println(maps);
//        return maps;
//    }

//    @GetMapping("/search/kp/{name}/{pageNumber}/{pageSize}")
//    public PageQueryQuestionResult pageSearchQuestionByKnowledgePoint(@PathVariable(value = "name") String name
//            , @PathVariable(value = "pageNumber") Long pageNumber
//            , @PathVariable(value = "pageSize") Long pageSize) throws JsonProcessingException {
//        //System.out.println(pageNumber);
//        return questionService.pageQueryQuestionsByKnowledgePoint(name, pageNumber, pageSize);
//    }

//    @GetMapping("/search/kp/{name}/{pageNumber}/{pageSize}")
//    public PageQueryQuestionResult searchQuestionByKnowledgePoint(@PathVariable(value = "name") String name
//            , @PathVariable(value = "pageNumber") Long pageNumber
//            , @PathVariable(value = "pageSize") Long pageSize)
//            throws IOException {
//        System.out.println(name);
//
//        return  mongoQuestionService.queryQuestionsByKnowledgePointNames(List.of(name.strip()),
//                pageNumber,
//                pageSize);
//    }
    @GetMapping("/search/kp/{name}/{pageNumber}/{pageSize}")
    public PageQueryQuestionResult searchQuestionByKnowledgePoint(@PathVariable(value = "name") String name
            , @PathVariable(value = "pageNumber") Long pageNumber
            , @PathVariable(value = "pageSize") Long pageSize)
            throws IOException {
        System.out.println(name);

        PageQueryQuestionResult pageQueryQuestionResult = esQuestionService.queryQuestionsByKnowledgePointNames(List.of(name.strip()),
                pageNumber,
                pageSize);

        System.out.println(pageQueryQuestionResult);

        return  pageQueryQuestionResult;
    }

    @GetMapping("/search/keyword/{name}/{pageNumber}/{pageSize}")
    public PageQueryQuestionResult searchQuestionByKeyword(@PathVariable(value = "name") String name
            , @PathVariable(value = "pageNumber") Long pageNumber
            , @PathVariable(value = "pageSize") Long pageSize)
            throws IOException {

        //System.out.println(pageQueryQuestionResult);
        System.out.println(name);
        return esQuestionService.queryQuestionsByKeyword(name.strip(),
                pageNumber,
                pageSize);
    }
}
