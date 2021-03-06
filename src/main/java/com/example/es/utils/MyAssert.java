package com.example.es.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 定义方法
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @个人博客 https://choleen95.github.io/
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/1 23:05
 */
public class MyAssert extends Assert {
    public static void isEmpty(Object object ,String message){
        if(object == null){
            throw new IllegalArgumentException(message);
        }
        if(object instanceof String){
            String check = object.toString();
            if("".equals(check)){
                throw new IllegalArgumentException(message);
            }
        }
    }
}
