package com.ht.bnu_tiku_backend.utils.DTO;

import lombok.Data;

@Data
public class LoginResultDTO {
    private String token;
    private UserDTO userInfo;
}
