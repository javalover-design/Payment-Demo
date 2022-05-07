package com.example.service.impl;

import com.example.config.WxPayConfig;
import com.example.entity.OrderInfo;
import com.example.enums.OrderStatus;
import com.example.enums.StatusCode;
import com.example.enums.wxpay.WxApiType;
import com.example.enums.wxpay.WxNotifyType;
import com.example.service.WxPayService;
import com.example.util.OrderNoUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lambda
 */
@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    //注入参数信息
    @Resource
    private WxPayConfig wxPayConfig;

    //注入http客户端参数，用于更新签名证书并完成请求
    @Resource
    private CloseableHttpClient httpClient;

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
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTitle("test");
        //订单号
        orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo.setTotalFee(1);
        orderInfo.setProductId(productId);
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());


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
           String code_url = resultMap.get("code_url");

           //使用map将所需要的信息返回
            Map<String, Object> codeUrlAndOrderNoMap=new HashMap<String, Object>();
            codeUrlAndOrderNoMap.put("codeUrl",code_url);
            codeUrlAndOrderNoMap.put("orderInfoNo",orderInfo.getOrderNo());

            return codeUrlAndOrderNoMap;
            //将结果返回
        }finally {
            response.close();
        }

    }
}
