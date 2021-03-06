package com.example.es;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2020/12/30 0:12
 */
public class Demo {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    @Test
    public void demo(){
       /* String s = UUID.randomUUID().toString();
        logger.info(s);*/
        List list1 = new ArrayList();
        list1.add(0);
        List list2 = list1;
        System.out.println(list1.get(0) instanceof Integer);
        System.out.println(list2.get(0) instanceof Integer);
    }

    @Test
    public void demo1(){
        Integer total = 1043;
        double pointNumber = (double)total/1000;
        System.out.println(pointNumber);
    }

}
