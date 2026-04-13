package com.ISC.llm.service.llm;

import com.ISC.llm.model.dto.ChatRequest;
import com.ISC.llm.model.dto.ChatResponse;
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
public class OpenAIClient {

    private final RestTemplate restTemplate;

    @Value("${llm.openai.api-key:your-api-key-here}")
    private String apiKey;

    @Value("${llm.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    public ChatResponse chatCompletion(ChatRequest request) {
        String url = baseUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.info("📡 Calling Upstream LLM Provider: {} | Model: {}", baseUrl, request.getModel());
            return restTemplate.postForObject(url, entity, ChatResponse.class);
        } catch (Exception e) {
            log.error("❌ Error calling OpenAI API: {}", e.getMessage());
            throw new RuntimeException("LLM Provider Error: " + e.getMessage());
        }
    }
}
