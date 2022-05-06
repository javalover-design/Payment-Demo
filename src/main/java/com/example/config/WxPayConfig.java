package com.example.config;

import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.PrivateKey;

/**
 * @author lambda
 */
@Configuration
@ConfigurationProperties(prefix = "wxpay")
@PropertySource("classpath:wxpay.properties")
@Data
public class WxPayConfig {

    /**
     * 商户号
     */
    private String mchId;
    /**
     * 商户API证书序列号
     */
    private String mchSerialNo;
    /**
     * 商户私钥文件
     */
    private String privateKeyPath;
    /**
     * APIV3密钥
     */
    private String apiV3Key;
    /**
     * APPID
     */
    private String appID;
    /**
     * 微信服务器地址
     */
    private String domain;
    /**
     * 接收结果通知地址
     */
    private String notifyDomain;


    /**
     * 获取用户的私钥文件，并且只在config类的内部使用
     * @param filePath 私钥文件路径
     * @return
     */
    private PrivateKey getPrivateKey(String filePath){
        PrivateKey privateKey=null;
        try {
             privateKey = PemUtil.loadPrivateKey(new FileInputStream(filePath));

        } catch (FileNotFoundException e) {
            throw new RuntimeException("私钥文件不存在",e);
        }
        return privateKey;

    }

}
