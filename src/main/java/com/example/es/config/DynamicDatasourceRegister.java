package com.example.es.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.example.es.utils.MyAssert;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * @description 数据源配置
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/1 22:02
 */
@Configuration
@Component
@MapperScan(basePackages = {"com.example.es.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
@PropertySource(value = "classpath:application.yml")
public class DynamicDatasourceRegister implements EnvironmentAware{
    private static final Logger logger = LoggerFactory.getLogger(DynamicDatasourceRegister.class);


    private static  String PREFIX = "spring.datasource";

    @Override
    public void setEnvironment(Environment env) {

        init(env);
    }

    private Map<Object, Object> targetDataSources = new HashMap<>();

    private Object defaultTargetDataSource;

    @Bean("dynamicDataSource")
    public DynamicDatasource init(Environment env){
        String datasourceNames = env.getProperty(PREFIX+".dynamicNames");
        MyAssert.isEmpty(datasourceNames,"dataSource is null");
        String[] array = datasourceNames.split(",");
        DynamicDatasource dynamicDatasource = new DynamicDatasource();
        if(array.length == 1){
            //只有一个
            String type = array[0];
            DataSource dataSource = buildDataSource(env, type);
            targetDataSources.put(type,dataSource);
            defaultTargetDataSource = dataSource;
        }else {
            //多个
            for (String s : array) {
                DataSource dataSource = buildDataSource(env, s);
                targetDataSources.put(s,dataSource);
            }
        }
       dynamicDatasource.setTargetDataSources(targetDataSources);
       if(defaultTargetDataSource == null){
           defaultTargetDataSource = targetDataSources.get(array[0]);
           DynamicDataSourceContextHolder.setRouteKey(array[0]);
       }
       dynamicDatasource.setDefaultTargetDataSource(defaultTargetDataSource);
        return dynamicDatasource;
    }


    public DataSource buildDataSource(Environment props, String type){
        String dbNames = PREFIX+"."+type+ ".";
        String url = props.getProperty(dbNames+"url");
        String username = props.getProperty(dbNames+"username");
        String password = props.getProperty(dbNames+"password");
        String driverClassName = props.getProperty(dbNames+"driver-class-name");
        logger.info("加载数据源--------------->{}",url);
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(@Autowired @Qualifier("dynamicDataSource") DynamicDatasource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**.xml"));
        return sqlSessionFactoryBean.getObject();
    }

}
