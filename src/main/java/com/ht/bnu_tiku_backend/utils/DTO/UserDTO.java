package com.ht.bnu_tiku_backend.utils.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    private String userName;      // 昵称

    private String userAccount;   // 登录账号

    private String userRole;      // 角色：student / teacher / admin

    private String avatarUrl;     // 头像地址（可选）
}
