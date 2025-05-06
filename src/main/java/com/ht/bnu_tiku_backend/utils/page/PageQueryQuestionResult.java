package com.ht.bnu_tiku_backend.utils.page;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Data
public class PageQueryQuestionResult {
    Long pageNo;

    Long pageSize;

    Long totalCount;

    List<Map<String, String>> questions;
}
