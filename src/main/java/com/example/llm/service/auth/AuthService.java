package com.example.llm.service.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.llm.config.security.JwtUtils;
import com.example.llm.entity.User;
import com.example.llm.mapper.UserMapper;
import com.example.llm.model.dto.auth.LoginReqDTO;
import com.example.llm.model.dto.auth.LoginRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginRespDTO login(LoginReqDTO reqDTO) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, reqDTO.getUsername())
                .eq(User::getStatus, 1));
                
        if (user == null || !passwordEncoder.matches(reqDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        
        return LoginRespDTO.builder()
                .token(token)
                .expireIn(86400L)
                .build();
    }

    public void register(com.example.llm.model.dto.auth.RegisterReqDTO reqDTO) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, reqDTO.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(reqDTO.getUsername());
        user.setPassword(passwordEncoder.encode(reqDTO.getPassword()));
        user.setNickname(reqDTO.getNickname());
        user.setEmail(reqDTO.getEmail());
        user.setStatus(1);
        
        userMapper.insert(user);
    }
}
