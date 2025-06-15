package com.ht.bnu_tiku_backend.utils.request;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String userName;

    private String email;

    private String userPassword;

    private String checkPassword;
}
