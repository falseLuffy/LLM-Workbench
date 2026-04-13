package com.ISC.llm.model.dto.user;

import lombok.Data;

@Data
public class UpdatePasswordReqDTO {
    private String oldPassword;
    private String newPassword;
}
