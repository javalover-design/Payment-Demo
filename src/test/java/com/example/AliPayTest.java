package com.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class AliPayTest {

    /**
     * 引入springboot为我们提供的环境，会自动读取所有的配置信息（配置了propertiesSource）
     */
    @Resource
    private Environment config;

    @Test
    public void  getProperties(){
        log.info(config.getProperty("alipay.app-id"));

    }

}
