package com.example.es.mapper;

import com.example.es.pojo.Role;
import com.example.es.pojo.User;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Choleen
 */

public interface UserMapper extends Mapper<User> {

    List<User> getUserAll();

    User selectUser(String username);

    List<Role> selectRole(Integer userId);

    int save(User user);
}
