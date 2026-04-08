package com.example.llm.service.llm;

import com.example.llm.model.dto.ChatRequest;
import com.example.llm.model.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekClient {

    private final RestTemplate restTemplate;

    @Value("${llm.deepseek.api-key:sk-your-deepseek-key}")
    private String apiKey;

    @Value("${llm.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    public ChatResponse chatCompletion(ChatRequest request) {
        String url = baseUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.info("📡 正在调用 DeepSeek 上游接口: {} | 模型: {}", baseUrl, request.getModel());
            return restTemplate.postForObject(url, entity, ChatResponse.class);
        } catch (Exception e) {
            log.error("❌ 调用 DeepSeek 接口失败: {}", e.getMessage());
            throw new RuntimeException("DeepSeek 接口异常: " + e.getMessage());
        }
    }
}
