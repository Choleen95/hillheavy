package com.example.es.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步执行线程池
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @个人博客 https://choleen95.github.io/
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/7 23:13
 */
public class AsynExecutorUtil {

    private static final   ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static ExecutorService getPool(){
        return executorService;
    }

    public static void execute(Runnable task){
        getPool().submit(task);
    }
}
