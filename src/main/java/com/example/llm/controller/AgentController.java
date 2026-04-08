package com.example.llm.controller;

import com.example.llm.agent.WorkAssistantAgent;
import com.example.llm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 智能体", description = "与具备业务操作和知识库能力的 AI 助手交互")
@RestController
@RequestMapping("/v1/agent")
@RequiredArgsConstructor
public class AgentController {

    private final WorkAssistantAgent agent;

    @Operation(summary = "咨询办公助手（支持 RAG 与 数据库查询）")
    @GetMapping("/ask")
    public Result<String> askAgent(@RequestParam String prompt) {
        String response = agent.chat(prompt);
        return Result.success(response);
    }
}
