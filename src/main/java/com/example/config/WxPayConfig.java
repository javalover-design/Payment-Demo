package com.example.config;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * @author lambda
 *
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

    //用于签名验证的方法
    /**
     * 获取签名验证器的例子
     * @return
     */
    @Bean
    public ScheduledUpdateCertificatesVerifier getScheduledUpdateCertificatesVerifier(){
        //获取商户密钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);

        //new PrivateKeySigner(mchSerialNo,privateKey) 获取私钥签名对象
        // new WechatPay2Credentials(mchId,new PrivateKeySigner(mchSerialNo,privateKey))身份认证对象

        //apiV3Key.getBytes(StandardCharsets.UTF_8)当数据量大的时候，使用对称加密，提高效率
        //使用定时更新的签名验证器，不需要传入证书
        ScheduledUpdateCertificatesVerifier verifier=new ScheduledUpdateCertificatesVerifier(
                new WechatPay2Credentials(mchId,new PrivateKeySigner(mchSerialNo,privateKey)),
                apiV3Key.getBytes(StandardCharsets.UTF_8)
        );
        return verifier;

    }


    /**
     *获取http请求对象
     * @param verifier 验证器对象
     * @return
     */
    @Bean
    public CloseableHttpClient getWxPayCloseableHttpClient(ScheduledUpdateCertificatesVerifier verifier){

        WechatPayHttpClientBuilder builder=WechatPayHttpClientBuilder.create()
                .withMerchant(mchId,mchSerialNo,getPrivateKey(privateKeyPath))
                .withValidator(new WechatPay2Validator(verifier));

        //通过 WechatPayHttpClientBuilder构造的httpClient会自动处理签名和验签，并进行证书自动更新
        CloseableHttpClient httpClient = builder.build();

        return httpClient;

    }

}
