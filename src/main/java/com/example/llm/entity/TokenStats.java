package com.example.llm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("token_stats")
public class TokenStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime requestTime;
    private Integer isCacheHit;
    private String originalModel;
    private String usedModel;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer savedTokens;
}
