package com.ht.bnu_tiku_backend.mongodb.service;

import com.ht.bnu_tiku_backend.mongodb.model.User;

import java.util.List;

public interface MongoUserService {
    void saveUser(User user);

    List<User> getAllUsers();
}