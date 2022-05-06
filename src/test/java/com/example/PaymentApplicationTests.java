package com.example;

import com.example.config.WxPayConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootTest
class PaymentApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Resource
    private WxPayConfig wxPayConfig;

    @Test
    void contextLoads() {
        System.out.println(dataSource.getClass());

    }

    //@Test
    //void getPrivateKey(){
    //    String privateKeyPath = wxPayConfig.getPrivateKeyPath();
    //
    //    PrivateKey privateKey = wxPayConfig.getPrivateKey(privateKeyPath);
    //    System.out.println(privateKey);
    //}

}
