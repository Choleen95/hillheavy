package com.example.es.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 根据当前线程，获取数据源类型,，实现数据源切换
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/1 21:56
 */
public class DynamicDatasource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceRouteKey.getRouteKey();
    }
}
