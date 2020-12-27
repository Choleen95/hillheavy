package com.example.es.service;

import com.example.es.pojo.MyUserDetails;
import com.example.es.pojo.User;
import com.example.es.vo.ResponseBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * @author Choleen
 */
public interface UserService {

    List<User> getUserList();

    ResponseBean doRegister(String username, String password, String confirmPassword);
}
