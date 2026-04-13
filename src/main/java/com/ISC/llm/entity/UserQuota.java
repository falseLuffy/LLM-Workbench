package com.ISC.llm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

@Data
@TableName("user_quota")
public class UserQuota {
    @TableId
    private Long userId;
    private Long totalTokens;
    private Long usedTokens;
    
    @Version
    private Integer version;
}
