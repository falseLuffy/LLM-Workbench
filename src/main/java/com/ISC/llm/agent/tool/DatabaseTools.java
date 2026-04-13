package com.ISC.llm.agent.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ISC.llm.entity.User;
import com.ISC.llm.mapper.UserMapper;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseTools {

    private final UserMapper userMapper;

    @Tool("查询系统中所有活跃（未删除）用户的总数")
    public Long getTotalUserCount() {
        log.info("🤖 智能体正在调用工具: 查询用户总数");
        return userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
    }

    @Tool("根据用户名或昵称搜索用户信息")
    public String searchUserByName(String name) {
        log.info("🤖 智能体正在调用工具: 搜索用户 [ {} ]", name);
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .like(User::getUsername, name)
                .or()
                .like(User::getNickname, name));
        
        if (users.isEmpty()) {
            return "未找到匹配用户。";
        }
        
        return users.stream()
                .map(u -> String.format("ID: %d, 用户名: %s, 昵称: %s, 邮箱: %s, 注册时间: %s", 
                        u.getId(), u.getUsername(), u.getNickname(), u.getEmail(), u.getCreateTime()))
                .collect(Collectors.joining("\n"));
    }
}
