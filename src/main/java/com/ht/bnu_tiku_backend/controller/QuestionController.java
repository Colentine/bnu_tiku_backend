package com.ht.bnu_tiku_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsQuestionServiceImpl;
import com.ht.bnu_tiku_backend.mongodb.service.MongoQuestionService;
import com.ht.bnu_tiku_backend.service.QuestionService;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import com.ht.bnu_tiku_backend.utils.request.QuestionCorrectRequest;
import com.ht.bnu_tiku_backend.utils.request.QuestionSearchRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class QuestionController {
    @Resource
    QuestionService questionService;
    @Resource
    EsQuestionServiceImpl esQuestionService;
    /**
     * sql知识点查题
     * @param name
     * @return
     * @throws JsonProcessingException
     */
    public List<Map<String, String>> mySQLSearchQuestionByKnowledgePoint(@PathVariable(value = "name") String name)
            throws JsonProcessingException {
        log.info("知识点名：{}", name);
        return questionService.queryQuestionsByKnowledgePoint(name);
    }
    /**
     * sql知识点分页查询
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws JsonProcessingException
     */
    public PageQueryQuestionResult mySQLPageSearchQuestionByKnowledgePoint(@PathVariable(value = "name") String name
            , @PathVariable(value = "pageNumber") Long pageNumber
            , @PathVariable(value = "pageSize") Long pageSize) throws JsonProcessingException {
        log.info("知识点名称：{} 页号：{} 页大小：{}", name, pageNumber, pageSize);
        return questionService.pageQueryQuestionsByKnowledgePoint(name, pageNumber, pageSize);
    }
//    /**
//     * mongo知识点分页查题
//     * @param name
//     * @param pageNumber
//     * @param pageSize
//     * @return
//     * @throws IOException
//     */
//    public PageQueryQuestionResult mongoSearchQuestionByKnowledgePoint(@PathVariable(value = "name") String name
//            , @PathVariable(value = "pageNumber") Long pageNumber
//            , @PathVariable(value = "pageSize") Long pageSize)
//            throws IOException {
//
//        PageQueryQuestionResult pageQueryQuestionResult = mongoQuestionService.queryQuestionsByKnowledgePointNames(List.of(name.strip()),
//                pageNumber,
//                pageSize);
//        log.info("知识点名称：{} 页号：{} 页大小：{} 按知识点查询结果：{}", name, pageNumber, pageSize, pageQueryQuestionResult);
//        return pageQueryQuestionResult;
//    }
    /**
     * elasticsearch知识点分页查题
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/search/kp/{name}/{pageNumber}/{pageSize}")
    public Result<PageQueryQuestionResult> esSearchQuestionByKnowledgePoint(@PathVariable(value = "name") String name
            , @PathVariable(value = "pageNumber") Long pageNumber
            , @PathVariable(value = "pageSize") Long pageSize) {
        log.info("知识点名称：{} 页号：{} 页大小：{}", name, pageNumber, pageSize);
        return esQuestionService.queryQuestionsByKnowledgePointNames(List.of(name.strip()),
                pageNumber,
                pageSize);
    }
    /**
     * elasticsearch关键词分页查题
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws IOException
     * @author: ht
     */
    @GetMapping("/search/keyword/{name}/{pageNumber}/{pageSize}")
    public PageQueryQuestionResult esSearchQuestionByKeyword(@PathVariable(value = "name") String name
            , @PathVariable(value = "pageNumber") Long pageNumber
            , @PathVariable(value = "pageSize") Long pageSize)
            throws IOException {
        log.info("知识点名称：{} 页号：{} 页大小：{}", name, pageNumber, pageSize);
        return esQuestionService.queryQuestionsByKeyword(name.strip(),
                pageNumber,
                pageSize);
    }
    /**
     * elasticsearch 组合条件查题
     * @param questionSearchRequest
     * @return
     */
    @PostMapping("/search/combination")
    public Result<PageQueryQuestionResult> esSearchQuestionByCombination(@RequestBody QuestionSearchRequest questionSearchRequest) {
        log.info("组合条件：{}", questionSearchRequest);
        return esQuestionService.searchQuestionByCombination(questionSearchRequest);
    }
    /**
     * 批量导出题目
     * @param format
     * @param ids
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/export")
    public ResponseEntity<InputStreamResource> export(
            @RequestParam String format,
            @RequestBody List<Long> ids) throws IOException {
        File file;
        String mime;
        log.info("格式：{}， 题目编号：{}", format, ids);
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
    /**
     * 题目修改
     * @param questionCorrectRequest
     * @return
     */
    @PostMapping("/correct")
    public Result<String> correct(@RequestBody QuestionCorrectRequest questionCorrectRequest) {
        return esQuestionService.questionCorrect(questionCorrectRequest);
    }
}
