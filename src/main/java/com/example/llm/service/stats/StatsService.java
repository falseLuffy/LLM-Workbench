package com.example.llm.service.stats;

import com.example.llm.entity.TokenStats;
import com.example.llm.mapper.TokenStatsMapper;
import com.example.llm.model.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final TokenStatsMapper tokenStatsMapper;

    @Async
    public void recordStats(String originalModel, String usedModel, boolean isCacheHit, ChatResponse response, int savedTokens) {
        try {
            int promptTokens = 0;
            int completionTokens = 0;
            
            if (response != null && response.getUsage() != null) {
                promptTokens = response.getUsage().getPromptTokens();
                completionTokens = response.getUsage().getCompletionTokens();
            }

            TokenStats stats = TokenStats.builder()
                    .requestTime(LocalDateTime.now())
                    .originalModel(originalModel)
                    .usedModel(usedModel)
                    .isCacheHit(isCacheHit ? 1 : 0)
                    .promptTokens(promptTokens)
                    .completionTokens(completionTokens)
                    .savedTokens(savedTokens)
                    .build();

            tokenStatsMapper.insert(stats);
            log.info("📊 统计已记录: 是否缓存命中={}, 节省 Token={}", isCacheHit, savedTokens);
        } catch (Exception e) {
            log.error("记录 Token 统计失败", e);
        }
    }

    public Map<String, Object> getOverallStats() {
        return tokenStatsMapper.getGlobalStats();
    }
}
