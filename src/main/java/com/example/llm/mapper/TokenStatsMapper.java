package com.example.llm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.llm.entity.TokenStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface TokenStatsMapper extends BaseMapper<TokenStats> {

    @Select("SELECT " +
            "COUNT(*) as total_requests, " +
            "SUM(is_cache_hit) as total_cache_hits, " +
            "SUM(prompt_tokens) as total_prompt_tokens, " +
            "SUM(completion_tokens) as total_completion_tokens, " +
            "SUM(saved_tokens) as total_saved_tokens " +
            "FROM token_stats")
    Map<String, Object> getGlobalStats();
}
