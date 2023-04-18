package com.example.es.service.impl;

import com.example.es.service.RedisService;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

/**
 * 功能描述
 *  Redis服务实现类
 *
 * @author [山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2023/4/18 21:49]
 */
public class RedisServiceImpl implements RedisService {
    /**
     * 设置key前缀
     */
    private static final String LOCK_PREFIX = "LOCK:CUSTOMER";
    /**
     * 设置默认失效时间
     */
    private static final long DEFAULT_LOCK_EXPIRE_TIME = 5;

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public <T> T callWithLock(String key, Callable<T> callable) throws Exception {
        String lockKey = String.format("%s:%s",LOCK_PREFIX, Thread.currentThread());
        // 将UUID作为删除key时的判断依据
        String value = UUID.randomUUID().toString();
        try {
            // 获取锁失败
            if (!lock(lockKey, value)) {
                throw new Exception("获取锁失败");
            }
            // 获取锁成功执行功能
            return callable.call();
        } finally {
            // 释放锁
            unlock(lockKey, value);
        }

    }

    /**
     *  使用redis的lua命令进行查询、删除key操作
     *  使其具备原子性(最好是在redis上创建lua脚本，执行到pipe缓存中)
     *
     * @param key redis的key
     * @param value redis的value
     * @author 山沉
     * @date 2023/4/18 22:17
     * @return {@link Boolean}
     */
    private boolean unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[2] then"
                + " return redis.call('del', KEYS[1])"
                + " else return 0 end";
        try {
            Boolean unlockState = (Boolean)redisTemplate.execute((RedisCallback<Boolean>) action -> action.eval(script.getBytes(), ReturnType.BOOLEAN, 1,
                     key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8)));
            return unlockState == null || !unlockState;
        } catch (Exception e) {
            logger.error("释放锁失败,key={},value={}", key, value);
            return false;
        }
    }

    private boolean lock(String lockKey, String value) {
        Boolean flag = false;
        try {
            flag = (Boolean)redisTemplate.execute((RedisCallback<Boolean>)connection -> connection.set(lockKey.getBytes(
                    StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8), Expiration
                            .from(DEFAULT_LOCK_EXPIRE_TIME, TimeUnit.SECONDS),
                    SetOption.SET_IF_ABSENT));
        } catch (Exception e) {
            logger.error("获取锁失败：key={},value={}",lockKey, value);
            flag = false;
        }
        return flag != null && flag;
    }
}
