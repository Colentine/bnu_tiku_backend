package com.ht.bnu_tiku_backend.mongodb.repository;

import com.ht.bnu_tiku_backend.mongodb.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}