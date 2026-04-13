package com.ISC.llm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ISC.llm.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
