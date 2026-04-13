package com.example.llm.model.dto.auth;

import lombok.Data;

@Data
public class RegisterReqDTO {
    private String username;
    private String password;
    private String nickname;
    private String email;
}
