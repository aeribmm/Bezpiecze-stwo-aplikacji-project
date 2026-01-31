package com.s58703.demo.service;

import com.s58703.demo.entities.User;
import com.s58703.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public void save(User user){
        userRepository.save(user);
    }
}
