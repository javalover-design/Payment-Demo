package com.example.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lambda
 */

@Configuration //定义为配置类
@MapperScan("com.example.mapper") //扫描mapper接口
@EnableTransactionManagement //启用事务管理（spring-tx）
public class MyBatisPlusConfig {
}
