package com.example.es.controller;

import com.example.es.pojo.User;
import com.example.es.service.IndexService;
import com.example.es.service.impl.IndexServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private IndexService indexService;

    @GetMapping("queryUser")
    public List<User> queryUser(){
        return indexService.queryUser();
    }

    @PostMapping("bulkCreateDocument")
    public void bulkCreateDocument(String type){
        indexService.batchInsert(type);
    }

    @GetMapping("query")
    public List<User> queryByEs() {
        return indexService.queryByEs();
    }

}
