package com.example.llm.controller;

import com.example.llm.common.Result;
import com.example.llm.entity.ApiKey;
import com.example.llm.mapper.ApiKeyMapper;
import com.example.llm.service.auth.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "开发者凭证服务", description = "用于管理代理层调用的 API Key")
@RestController
@RequestMapping("/v1/apikey")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyMapper apiKeyMapper;
    private final ApiKeyService apiKeyService;

    @Operation(summary = "为主体用户生成全新的 API Key")
    @PostMapping("/generate")
    public Result<String> generateApiKey(@RequestParam(defaultValue = "默认密钥") String name) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String sku = "sk-" + UUID.randomUUID().toString().replace("-", "");

        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(currentUserId);
        apiKey.setKeyValue(sku);
        apiKey.setName(name);
        apiKey.setStatus(1);
        
        apiKeyMapper.insert(apiKey);

        return Result.success(sku);
    }

    @Operation(summary = "列出当前用户的 API Key")
    @GetMapping("/list")
    public Result<List<ApiKey>> listApiKeys() {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(apiKeyService.listApiKeys(currentUserId));
    }

    @Operation(summary = "删除指定的 API Key")
    @PostMapping("/delete")
    public Result<Void> deleteApiKey(@RequestParam Long id) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        apiKeyService.deleteApiKey(id, currentUserId);
        return Result.success();
    }
}
