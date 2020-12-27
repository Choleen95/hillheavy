package com.example.es.mapper;

import com.example.es.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Choleen
 */
@Mapper
public interface IndexMapper {

    /**
     * dddd
     * @param
     * @author Choleen
     * @date 2020/12/25 23:06
     * @return {@link List<User>}
     */
    public List<User> selectOne();
}
