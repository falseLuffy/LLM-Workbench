package com.example.llm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.llm.entity.ApiKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
}
