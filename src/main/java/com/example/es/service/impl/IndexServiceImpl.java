package com.example.es.service.impl;

import com.example.es.mapper.IndexMapper;
import com.example.es.pojo.User;
import com.example.es.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Choleen
 * @since 2020/12/25 22:52
 **/
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private IndexMapper indexMapper;

    @Override
    public List<User> queryUser(){
        return indexMapper.selectOne();
    }
}
