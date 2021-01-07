package com.example.es.mapper;

import com.example.es.pojo.InternetInfo;
import com.example.es.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 查询所有的数据
     * @param page 页
     * @param  number 条数
     * @author 山沉
     * @date 2021/1/8 0:33
     * @return {@link List< InternetInfo>}
     */
    List<InternetInfo> queryInternetInfoList(@Param("page")Integer page,@Param("number")Integer number);

    /**
     * 查询总记录
     * @param null
     * @author 山沉
     * @date 2021/1/8 0:42
     * @return {@link Integer}
     */
    Integer queryCountByInternetInfo();
}
