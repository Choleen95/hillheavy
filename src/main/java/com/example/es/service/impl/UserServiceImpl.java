package com.example.es.service.impl;

import com.example.es.config.DynamicDataSourceRouteKey;
import com.example.es.mapper.UserMapper;
import com.example.es.pojo.MyUserDetails;
import com.example.es.pojo.Role;
import com.example.es.pojo.User;
import com.example.es.service.UserService;
import com.example.es.vo.ResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import java.util.List;
import java.util.Objects;

/**
 * @author Choleen
 * @since 2020/12/26 0:20
 **/
@Service
public class UserServiceImpl implements UserDetailsService , UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> getUserList(String type){
        DynamicDataSourceRouteKey.setRouteKey(type);
        logger.info("数据源---------------->{}", DynamicDataSourceRouteKey.getRouteKey());
        return userMapper.getUserAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectUser(username);
        MyUserDetails details = new MyUserDetails();
        if(user == null){
            throw new BadCredentialsException("this username or password is not true!");
        }
        details.setUser(user);
        Integer id = user.getId();
        List<Role> roles = userMapper.selectRole(id);
        details.setRoles(roles);
        return details;
    }

    @Override
    public ResponseBean doRegister(String username, String password, String confirmPassword) {
        if(Objects.isNull(password) || Objects.isNull(username)){
            return ResponseBean.error("parameters is illegality");
        }
        if(!password.equals(confirmPassword)){
            return ResponseBean.error("twice access to password is not same");
        }
        User user = new User();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String realPassword = encoder.encode(password);
        user.setUsername(username);
        user.setPassword(realPassword);
        user.setEnabled(1);
        user.setLocked(0);
        userMapper.save(user);
        return ResponseBean.ok("id--->"+user.getId());
    }


}
