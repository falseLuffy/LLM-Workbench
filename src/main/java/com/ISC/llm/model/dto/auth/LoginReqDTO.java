package com.ISC.llm.model.dto.auth;

import lombok.Data;

@Data
public class LoginReqDTO {
    private String username;
    private String password;
}
