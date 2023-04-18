package com.example.es.service;

import java.util.concurrent.Callable;

/**
 * 功能描述
 * redis请求服务
 *
 * @author [山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2023/4/18 21:48]
 */
public interface RedisService {
    <T> T callWithLock(String key, Callable<T> callable) throws Exception;
}
