package com.ht.bnu_tiku_backend.utils.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String userName;
    private String userPassword;
}
