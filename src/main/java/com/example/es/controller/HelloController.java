package com.example.es.controller;

import com.example.es.pojo.User;
import com.example.es.service.UserService;
import com.example.es.vo.ResponseBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Choleen
 * @since 2020/12/26 0:17
 **/
@RestController
public class HelloController {
    @Resource
    private UserService userService;

    @GetMapping("/sayHello")
    public String sayHello(){
        return "welcome to Elastic core center";
    }

    @GetMapping("/welcome/say")
    public ResponseBean welcome(){
        return ResponseBean.ok("you have role_user,access to method");
    }

    @GetMapping("/admin/say")
    public ResponseBean helloByAdmin(){
        return ResponseBean.ok("you has role_admin,access to method");
    }

    @GetMapping("/login")
    public ResponseBean login(){
        return ResponseBean.sendByCode("you not login ,please login now.",401);
    }


}
