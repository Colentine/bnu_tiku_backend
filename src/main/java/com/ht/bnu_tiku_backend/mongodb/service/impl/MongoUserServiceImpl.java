package com.ht.bnu_tiku_backend.mongodb.service.impl;

import com.ht.bnu_tiku_backend.mongodb.model.User;
import com.ht.bnu_tiku_backend.mongodb.repository.UserRepository;
import com.ht.bnu_tiku_backend.mongodb.service.MongoUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoUserServiceImpl implements MongoUserService {


    @Resource
    private UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
