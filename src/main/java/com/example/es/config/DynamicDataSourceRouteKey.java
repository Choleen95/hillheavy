package com.example.es.config;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/1 21:57
 */
public class DynamicDataSourceRouteKey {

    private static ThreadLocal<String> routeKey = new ThreadLocal<>();

    public static String getRouteKey() {
        return routeKey.get();
    }

    public static void setRouteKey(String type) {
        routeKey.set(type);
    }

    public static void clearContextHolder(){
        routeKey.remove();
    }
}
