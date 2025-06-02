package com.ht.bnu_tiku_backend.controller;

import com.ht.bnu_tiku_backend.elasticsearch.model.KnowledgePoint;
import com.ht.bnu_tiku_backend.elasticsearch.service.impl.EsKnowledgePointServiceImpl;
import com.ht.bnu_tiku_backend.mongodb.service.impl.MongoKnowledgePointServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kp")
public class KnowledgePointController {
    @Resource
    private MongoKnowledgePointServiceImpl mongoKnowledgePointService;

    @Resource
    private EsKnowledgePointServiceImpl esKnowledgePointService;

//    @GetMapping("/search/{name}")
//    public List<KnowledgePoint> autoCompleteSearch(@PathVariable("name") String name) {
//        System.out.println(name);
//        return mongoKnowledgePointService.autoCompleteSearch(name);
//    }

    @GetMapping("/search/{name}")
    public List<KnowledgePoint> autoCompleteSearch(@PathVariable("name") String name) {
        System.out.println(name);
        return esKnowledgePointService.autoCompleteSearch(name);
    }
}
