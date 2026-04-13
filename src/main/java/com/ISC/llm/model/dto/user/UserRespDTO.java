package com.ISC.llm.model.dto.user;

import lombok.Data;

@Data
public class UserRespDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private Integer status;
}
