package com.s58703.demo.controllers;

import com.s58703.demo.entities.User;
import com.s58703.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping("/api/users/create/")
    public void createUser(@RequestBody User user){
        userService.save(user);
    }

}
