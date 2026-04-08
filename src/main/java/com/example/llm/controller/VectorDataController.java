package com.example.llm.controller;

import com.example.llm.common.Result;
import com.example.llm.model.dto.ChatRequest;
import com.example.llm.model.dto.ChatResponse;
import com.example.llm.model.dto.InjectionData;
import com.example.llm.service.proxy.LlmOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Tag(name = "数据管理", description = "用于手动维护向量缓存协议的接口")
@RestController
@RequestMapping("/v1/proxy/data")
@RequiredArgsConstructor
public class VectorDataController {

    private final LlmOptimizationService optimizationService;

    @Operation(summary = "批量投喂预定义的问答对（Q&A）到向量缓存")
    @PostMapping("/inject")
    public Result<String> injectData(@RequestBody List<InjectionData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return Result.error("Data list is empty");
        }

        for (InjectionData item : dataList) {
            // Build a simulated ChatResponse
            ChatResponse mockResponse = ChatResponse.builder()
                    .choices(Collections.singletonList(ChatResponse.Choice.builder()
                            .message(ChatRequest.Message.builder()
                                    .role("assistant")
                                    .content(item.getAnswer())
                                    .build())
                            .build()))
                    .build();
            
            // Build a simulated ChatRequest
            ChatRequest mockRequest = ChatRequest.builder()
                    .messages(Collections.singletonList(ChatRequest.Message.builder()
                            .role("user")
                            .content(item.getQuestion())
                            .build()))
                    .build();

            // Save to Milvus via optimization service
            optimizationService.saveToCache(mockRequest, mockResponse);
        }
        
        return Result.success("Successfully injected " + dataList.size() + " records into semantic cache.");
    }
}
