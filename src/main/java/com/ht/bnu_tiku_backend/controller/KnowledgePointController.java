package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsKnowledgePointServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/kp")
public class KnowledgePointController {
    @Resource
    private EsKnowledgePointServiceImpl esKnowledgePointService;

//    @GetMapping("/search/{name}")
//    public List<KnowledgePoint> autoCompleteSearch(@PathVariable("name") String name) {
//        System.out.println(name);
//        return mongoKnowledgePointService.autoCompleteSearch(name);
//    }

    /**
     * 知识点自动补全搜索
     *
     * @param keyword
     * @return
     */
    @GetMapping("/search/{name}")
    public List<KnowledgePoint> autoCompleteSearch(@PathVariable("name") String keyword) {
        log.info("关键词={}", keyword);
        return esKnowledgePointService.autoCompleteSearch(keyword);
    }
}
