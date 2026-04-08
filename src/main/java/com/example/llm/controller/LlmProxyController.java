package com.example.llm.controller;

import com.example.llm.model.dto.ChatRequest;
import com.example.llm.model.dto.ChatResponse;
import com.example.llm.service.llm.OpenAIClient;
import com.example.llm.service.proxy.LlmOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "LLM Proxy", description = "Optimized LLM Gateway")
@RestController
@RequestMapping("/v1/proxy")
@RequiredArgsConstructor
public class LlmProxyController {

    private final LlmOptimizationService optimizationService;
    private final OpenAIClient openAIClient;

    @Operation(summary = "Chat Completion with Token Optimization")
    @PostMapping("/chat/completions")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        
        // 1. Check Semantic Cache
        ChatResponse cachedResponse = optimizationService.checkSemanticCache(request);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        // 2. Dynamic Model Routing
        optimizationService.routeModel(request);

        // 3. Prompt Compression
        optimizationService.compressPrompt(request);

        // 4. Call Upstream LLM
        ChatResponse response = openAIClient.chatCompletion(request);

        // 5. Asynchronous Cache Persistence (Simplified as sync here)
        optimizationService.saveToCache(request, response);

        return response;
    }
}
