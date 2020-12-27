package com.example.es.controller;

import com.example.es.pojo.User;
import com.example.es.service.impl.IndexServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Choleen
 * @since 2020/12/25 22:51
 **/
@RestController
@RequestMapping("/es/index/")
public class IndexController {

    @Resource
    private IndexServiceImpl indexService;

    @GetMapping("queryUser")
    public List<User> queryUser(){
        return indexService.queryUser();
    }
}
