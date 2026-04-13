package com.ISC.llm.controller;

import com.ISC.llm.model.dto.auth.LoginReqDTO;
import com.ISC.llm.model.dto.auth.LoginRespDTO;
import com.ISC.llm.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证服务", description = "用户登录注册及相关")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public LoginRespDTO login(@RequestBody LoginReqDTO reqDTO) {
        return authService.login(reqDTO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public com.ISC.llm.common.Result<Void> register(@RequestBody com.ISC.llm.model.dto.auth.RegisterReqDTO reqDTO) {
        authService.register(reqDTO);
        return com.ISC.llm.common.Result.success();
    }
}
