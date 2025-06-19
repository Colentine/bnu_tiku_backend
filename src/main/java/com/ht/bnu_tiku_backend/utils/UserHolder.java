package com.ht.bnu_tiku_backend.utils;

import com.ht.bnu_tiku_backend.utils.DTO.UserDTO;

public class UserHolder {

    private static final ThreadLocal<UserDTO> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static UserDTO getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void setUser(UserDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}
