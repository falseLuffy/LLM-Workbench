package com.example.llm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String model;            // 使用的模型名称 (如 deepseek-chat)
    private List<Message> messages; // 消息列表（对话上下文）
    private Double temperature;     // 采样温度 (0-2)
    private Double top_p;          // 核采样概率
    private Integer n;             // 为每条输入生成多少条结果
    private Boolean stream;         // 是否流式输出
    private List<String> stop;     // 停止生成序列
    private Integer max_tokens;    // 最大生成 Token 数
    private Double presence_penalty; // 话题存在惩罚
    private Double frequency_penalty; // 频率重复惩罚
    private Map<String, Integer> logit_bias; // 调整特定 Token 的出现概率
    private String user;           // 终端用户 ID

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private String name;
    }
}
