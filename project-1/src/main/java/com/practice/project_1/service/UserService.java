package com.practice.project_1.service;

import com.practice.project_1.model.Users;
import com.practice.project_1.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Object addUser(Users users){
        users.setMobileNumber(users.getMobileNumber());
        return userRepository.save(users);
    }

}
