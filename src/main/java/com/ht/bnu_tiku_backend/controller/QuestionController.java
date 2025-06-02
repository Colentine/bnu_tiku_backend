package com.ht.bnu_tiku_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsQuestionServiceImpl;
import com.ht.bnu_tiku_backend.mongodb.service.MongoQuestionService;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import com.ht.bnu_tiku_backend.utils.request.QuestionSearchRequest;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

//        System.out.println(pageQueryQuestionResult);

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

    @PostMapping("/search/combination")
    public PageQueryQuestionResult searchQuestionByCombination(@RequestBody QuestionSearchRequest questionSearchRequest)
    {
        System.out.println(questionSearchRequest);
        return esQuestionService.searchQuestionByCombination(questionSearchRequest);
    }


    @PostMapping(value = "/export")
    public ResponseEntity<InputStreamResource> export(
            @RequestParam String format,
            @RequestBody List<Long> ids) throws IOException {

        File file;
        String mime;
        if ("docx".equalsIgnoreCase(format)) {
            file = esQuestionService.generateDocx(ids);
            mime = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if ("pdf".equalsIgnoreCase(format)) {
            file = esQuestionService.generatePdf(ids);
            mime = "application/pdf";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "非法格式");
        }
        return buildFileResponse(file, mime);
    }

    private ResponseEntity<InputStreamResource> buildFileResponse(File file, String mime)
            throws IOException {

        InputStreamResource res = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8))
                .contentType(MediaType.parseMediaType(mime))
                .contentLength(file.length())
                .body(res);
    }
}
