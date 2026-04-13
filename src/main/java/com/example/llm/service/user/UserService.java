package com.example.llm.service.user;

import com.example.llm.entity.User;
import com.example.llm.mapper.UserMapper;
import com.example.llm.model.dto.user.UpdatePasswordReqDTO;
import com.example.llm.model.dto.user.UpdateUserReqDTO;
import com.example.llm.model.dto.user.UserRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserRespDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserRespDTO dto = new UserRespDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        return dto;
    }

    public void updateUserInfo(Long userId, UpdateUserReqDTO reqDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setNickname(reqDTO.getNickname());
        user.setEmail(reqDTO.getEmail());
        userMapper.updateById(user);
    }

    public void updatePassword(Long userId, UpdatePasswordReqDTO reqDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(reqDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(reqDTO.getNewPassword()));
        userMapper.updateById(user);
    }
}
