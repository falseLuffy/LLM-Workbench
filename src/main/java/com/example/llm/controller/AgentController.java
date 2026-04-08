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

@Tag(name = "Agent", description = "AI Agent Interaction")
@RestController
@RequestMapping("/v1/agent")
@RequiredArgsConstructor
public class AgentController {

    private final WorkAssistantAgent agent;

    @Operation(summary = "Ask the Office Assistant Agent")
    @GetMapping("/ask")
    public Result<String> askAgent(@RequestParam String prompt) {
        String response = agent.chat(prompt);
        return Result.success(response);
    }
}
