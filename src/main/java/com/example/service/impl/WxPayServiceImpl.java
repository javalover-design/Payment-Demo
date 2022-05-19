package com.example.service.impl;

import com.example.config.WxPayConfig;
import com.example.entity.OrderInfo;
import com.example.enums.OrderStatus;
import com.example.enums.StatusCode;
import com.example.enums.wxpay.WxApiType;
import com.example.enums.wxpay.WxNotifyType;
import com.example.service.OrderInfoService;
import com.example.service.PaymentInfoService;
import com.example.service.WxPayService;
import com.google.gson.Gson;
import com.mysql.cj.util.StringUtils;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lambda
 *
 */
@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    /**注入参数信息*/
    @Resource
    private WxPayConfig wxPayConfig;

    /**注入http客户端参数，用于更新签名证书并完成请求*/
    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private PaymentInfoService paymentInfoService;

    /**
     * 创建一把可重入锁，用于数据的所定
     */
    private final ReentrantLock lock=new ReentrantLock();

    /**
     * 该方法用于处理支付业务
     * 创建订单，调用native支付接口
     * @param productId
     * @return code_url以及订单号
     */
    @Override
    public Map<String, Object> nativePay(Long productId) throws Exception {
        log.info("生成订单");

        //生成订单
        //OrderInfo orderInfo = new OrderInfo();
        //orderInfo.setTitle("test");
        ////订单号
        //orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        //orderInfo.setTotalFee(1);
        //orderInfo.setProductId(productId);
        //orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
        OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);

        //此处进行判断，如果codeUrl与订单信息都不为空，则直接将原来的信息返回即可
        String codeUrl = orderInfo.getCodeUrl();

        if (orderInfo !=null && !StringUtils.isEmptyOrWhitespaceOnly(codeUrl)){
            log.info("二维码已经保存了.......");
            //使用map将所需要的信息返回
            Map<String, Object> codeUrlAndOrderNoMap=new HashMap<String, Object>();
            codeUrlAndOrderNoMap.put("codeUrl",codeUrl);
            codeUrlAndOrderNoMap.put("orderNo",orderInfo.getOrderNo());

            return codeUrlAndOrderNoMap;
        }


        log.info("调用统一下单API");
        //调用统一下单API
        HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.toString()));
         //请求体参数
        Gson gson = new Gson();
        Map paramsMap = new HashMap();
        //为参数赋予值
        paramsMap.put("appid",wxPayConfig.getAppID());
        paramsMap.put("mchid",wxPayConfig.getMchId());
        paramsMap.put("description",orderInfo.getTitle());
        paramsMap.put("out_trade_no",orderInfo.getOrderNo());
        paramsMap.put("notify_url",wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));

        //设置订单金额参数
        HashMap amountMap = new HashMap();
        amountMap.put("total",orderInfo.getTotalFee());
        amountMap.put("currency","CNY");

        paramsMap.put("amount",amountMap);

        //将参数的结果转换成json字符串
        String paramJson = gson.toJson(paramsMap);

        log.info("请求参数"+paramJson);

        //http请求的请求体
        StringEntity stringEntity = new StringEntity(paramJson, "UTF-8");
        stringEntity.setContentType("application/json");

        httpPost.setEntity(stringEntity);
        //希望接收的数据格式也是json
        httpPost.setHeader("Accept","application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            //设置字符串形式响应体
            String bodyAsString = EntityUtils.toString(response.getEntity());
            //获取请求的响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == StatusCode.SUCCESS.getCode()) {
                //处理成功
                log.info("success,return body=" + bodyAsString);
            } else if (statusCode == StatusCode.SUCCESS_NOT_BODY.getCode()) {
                //处理成功但是没有返回体
                log.info("success");

            } else {
                log.info("failed,resp code=" + statusCode + ",return Body" + bodyAsString);
                throw new IOException("Request failed");
            }

            //响应结果
            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);

            //从响应结果中解析二维码
          codeUrl = resultMap.get("code_url");
           //保存二维码
            String orderNo = orderInfo.getOrderNo();
            orderInfoService.saveCodeUrl(orderNo,codeUrl);


            //使用map将所需要的信息返回
            Map<String, Object> codeUrlAndOrderNoMap=new HashMap<String, Object>();
            codeUrlAndOrderNoMap.put("codeUrl",codeUrl);
            codeUrlAndOrderNoMap.put("orderNo",orderInfo.getOrderNo());

            return codeUrlAndOrderNoMap;
            //将结果返回
        }finally {
            response.close();
        }

    }

    @SneakyThrows
    @Override
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
            log.info("订单处理......");
            //获取到明文数据，进行订单处理
            String plainText=decryptFromResource(bodyMap);
            //此时明文仍旧是字符串形式，需要转换成map
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        //获取订单号
        String orderNo = (String) plainTextMap.get("out_trade_no");

        if (lock.tryLock()) {
            //进行尝试获取锁操作，成功获取则立即返回true，不会造成线程阻塞（synchronized会造成线程等待）

            try {


                //处理重复订单（如果商户没有反馈给微信信息，微信会重复发送通知）
                //根据订单号获取订单状态
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                //对订单状态进行判断，如果是支付了就直接返回，不处理
                if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
                    return;
                }

                //更新订单状态（针对数据库）,需要创建一个方法
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
                //记录支付日志,需要使用到PaymentInfoService类，专门记录支付日志信息
                paymentInfoService.createPaymentInfo(plainText);

            }finally {
                //释放锁
                lock.unlock();
            }
        }
    }


    /**
     * 用户取消订单功能
     * @param orderNo
     */
    @Override
    public void cancelOrder(String orderNo) throws IOException {
        log.info("用户取消订单....");
        //调用微信支付的关单接口（新建该类的私有方法，进行关单操作）
        this.closeOrder(orderNo);

        //更新商户端的订单状态(设置为用户已经取消)
        orderInfoService.updateStatusByOrderNo(orderNo,OrderStatus.CANCEL);



    }

    /**
     * 关单接口的调用
     * @param orderNo
     */
    private void closeOrder(String orderNo) throws IOException {
        log.info("关单接口调用,订单号====》{}",orderNo);
        //创建远程请求对象
        //从枚举中获取对应的关单地址，并且使用orderNo替换占位符
        String closeUrl = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
        //获取完整的地址(微信地址+关单地址)
        String url=wxPayConfig.getDomain().concat(closeUrl);
        //使用url创建请求
        HttpPost httpPost = new HttpPost(url);
        //组装json请求体
        Gson gson = new Gson();
        Map<String, String> paramMap = new HashMap<>();
        //设置参数
        paramMap.put("mchid",wxPayConfig.getMchId());
        //将需要的参数组装成json
        String jsonParams = gson.toJson(paramMap);
        log.info("请求参数-----》{}",jsonParams);
        //将请求参数设置到请求对象中
        StringEntity stringEntity = new StringEntity(jsonParams, StandardCharsets.UTF_8);
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept","application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {

            //获取请求的响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == StatusCode.SUCCESS.getCode()) {
                //处理成功
                log.info("success,return body=" );
            } else if (statusCode == StatusCode.SUCCESS_NOT_BODY.getCode()) {
                //处理成功但是没有返回体
                log.info("success");

            } else {
                log.info("failed,resp code=" + statusCode + ",return Body" );
                throw new IOException("Request failed");
            }
        }finally {
            response.close();
        }


    }

    /**
     * 对称解密处理
     * @param bodyMap
     * @return
     */
    private String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {
            log.info("密文解密.....");
            //从请求体中获取通知数据
       Map<String, String> resourceMap= (Map<String, String>)bodyMap.get("resource");
       //resourceMap中获取数据密文
        String ciphertext = resourceMap.get("ciphertext");
        //获取随机串
        String nonce = resourceMap.get("nonce");
        //获取附加数据
        String associatedData = resourceMap.get("associated_data");
        //创建密文解密工具,构造参数中需要密钥
        AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        //具体解密,得到明文数据
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        log.info(plainText);
        log.info("密文"+ciphertext);
        return plainText;
    }


}
