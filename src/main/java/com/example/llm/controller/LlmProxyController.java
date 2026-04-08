package com.example.llm.controller;

import com.example.llm.model.dto.ChatRequest;
import com.example.llm.model.dto.ChatResponse;
import com.example.llm.service.llm.DeepSeekClient;
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
@Tag(name = "LLM 中转网关", description = "支持 Token 优化、语义缓存和动态路由的高级网关")
@RestController
@RequestMapping("/v1/proxy")
@RequiredArgsConstructor
public class LlmProxyController {

    private final LlmOptimizationService optimizationService;
    private final DeepSeekClient deepSeekClient;
    private final com.example.llm.service.stats.StatsService statsService;

    @Operation(summary = "对话补全（带 Token 节流优化）")
    @PostMapping("/chat/completions")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String originalModel = request.getModel();
        
        // 1. 检查语义缓存
        ChatResponse cachedResponse = optimizationService.checkSemanticCache(request);
        if (cachedResponse != null) {
            statsService.recordStats(originalModel, "CACHE", true, cachedResponse, 100);
            return cachedResponse;
        }

        // 2. 动态模型路由
        optimizationService.routeModel(request);
        String usedModel = request.getModel();

        // 3. 提示词压缩
        optimizationService.compressPrompt(request);

        // 4. 调用上游大模型 API
        ChatResponse response = deepSeekClient.chatCompletion(request);

        // 5. 异步持久化到语义缓存
        optimizationService.saveToCache(request, response);

        // 6. 记录统计数据
        int savedTokens = usedModel.equals(originalModel) ? 0 : 50; 
        statsService.recordStats(originalModel, usedModel, false, response, savedTokens);

        return response;
    }
}
