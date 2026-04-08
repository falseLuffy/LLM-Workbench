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
    private String model;
    private List<Message> messages;
    private Double temperature;
    private Double top_p;
    private Integer n;
    private Boolean stream;
    private List<String> stop;
    private Integer max_tokens;
    private Double presence_penalty;
    private Double frequency_penalty;
    private Map<String, Integer> logit_bias;
    private String user;

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
