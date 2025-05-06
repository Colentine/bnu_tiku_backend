package com.ht.bnu_tiku_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.utils.LabelingTool.HmacSigner;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KnowledgeLabelingTest {
    private static final String APP_ID = "your_app_id";
    private static final String APP_SECRET = "your_app_secret";
    private static final String BASE_URL = "https://atms-api-test.100tal.com/atopen";
    private static final String PATH = "/questionKnowledge/predict";

    @Test
    public void labeling() throws Exception {
        // 构造请求体
        Map<String, Object> question = new HashMap<>();
        question.put("content", "已知2x+3=7，求x的值。");
        question.put("grade_group_name", "初中");
        question.put("question_id", UUID.randomUUID().toString());
        question.put("answer", "x=2");
        question.put("analysis", "移项后得2x=4，两边除以2得x=2");

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("top_k", Optional.of(5));
        requestBodyMap.put("question_list", List.of(question));

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestBodyMap);

        // 鉴权参数
        String method = "POST";
        String nonce = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());

        // 生成签名
        String signature = HmacSigner.generateSignature(APP_SECRET, method, PATH, timestamp, nonce, requestBody);

        // 构建请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + PATH)
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .addHeader("X-App-Id", APP_ID)
                .addHeader("X-Nonce", nonce)
                .addHeader("X-Timestamp", timestamp)
                .addHeader("X-Signature", signature)
                .build();

        System.out.println("🔐 Signature: " + signature);
        System.out.println("📤 Request JSON: " + requestBody);

        // 执行请求
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("✅ Response: " + response.body().string());
            } else {
                System.err.println("❌ Error: " + response.code() + " - " + response.body().string());
            }
        }
    }
}
