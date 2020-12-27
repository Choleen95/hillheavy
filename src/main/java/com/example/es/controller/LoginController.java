package com.example.es.controller;

import com.example.es.service.UserService;
import com.example.es.vo.ResponseBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Choleen
 * @since 2020/12/26 21:19
 **/
@RestController
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public ResponseBean register(String username,String password,String confirmPassword){
        return userService.doRegister(username,password,confirmPassword);
    }

}
