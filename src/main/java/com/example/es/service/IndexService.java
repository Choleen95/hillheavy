package com.example.es.service;

import com.example.es.pojo.User;

import java.util.List;

/**
 * @author Choleen
 */
public interface IndexService {

    /**
     * 查询用户
     * @author 山沉
     * @date 2021/1/7 22:36
     * @return {@link List<User>}
     */
    List<User> queryUser();

    /**
     * 根据百度百科，批量插入数据
     * @param type 数据源
     * @author 山沉
     * @date 2021/1/7 22:34
     * @return {@link }
     */
    void batchInsert(String type);
}
