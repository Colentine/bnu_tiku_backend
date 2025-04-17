package com.ht.bnu_tiku_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ht.bnu_tiku_backend.service.QuestionService;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.statement.select.Join;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question/")
public class QuestionController {
    @Resource
    QuestionService questionService;

    @GetMapping("/search/kp/{name}")
    public List<Map<String, String>> search(@PathVariable("name") String name) throws JsonProcessingException {
        return questionService.queryQuestionsByKnowledgePoint(name);
    }
}
