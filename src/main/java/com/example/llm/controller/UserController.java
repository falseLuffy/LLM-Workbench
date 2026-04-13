package com.example.llm.controller;

import com.example.llm.common.Result;
import com.example.llm.model.dto.user.UpdatePasswordReqDTO;
import com.example.llm.model.dto.user.UpdateUserReqDTO;
import com.example.llm.model.dto.user.UserRespDTO;
import com.example.llm.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户服务", description = "用户个人信息管理")
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<UserRespDTO> getUserInfo() {
        return Result.success(userService.getUserInfo(getCurrentUserId()));
    }

    @Operation(summary = "修改当前用户信息")
    @PostMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody UpdateUserReqDTO reqDTO) {
        userService.updateUserInfo(getCurrentUserId(), reqDTO);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordReqDTO reqDTO) {
        userService.updatePassword(getCurrentUserId(), reqDTO);
        return Result.success();
    }
}
