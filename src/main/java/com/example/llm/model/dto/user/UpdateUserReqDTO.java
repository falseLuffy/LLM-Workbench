package com.example.llm.model.dto.user;

import lombok.Data;

@Data
public class UpdateUserReqDTO {
    private String nickname;
    private String email;
}
