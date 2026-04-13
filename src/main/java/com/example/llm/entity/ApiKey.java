package com.example.llm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_key")
public class ApiKey {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String keyValue;
    private String name;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    
    @TableLogic
    private Integer deleted;
}
