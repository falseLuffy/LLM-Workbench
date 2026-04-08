package com.example.llm.controller;

import com.example.llm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "System", description = "System management endpoints")
@RestController
@RequestMapping("/sys")
public class HealthController {

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public Result<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Service is running correctly");
        return Result.success(status);
    }
}
