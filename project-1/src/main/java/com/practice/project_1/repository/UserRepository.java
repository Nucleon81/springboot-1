package com.practice.project_1.repository;

import com.practice.project_1.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Users, String> {
}
