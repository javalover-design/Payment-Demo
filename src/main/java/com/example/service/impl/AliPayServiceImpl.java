package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.entity.OrderInfo;
import com.example.service.AliPayService;
import com.example.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author lambda
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private  AlipayClient alipayClient;

    /**
     * 根据订单号创建订单并发起支付请求获取平台响应返回到前端
     * @param productId the product id
     * @return 返回支付请求调用的响应主体信息，返回到controller层
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String tradeCreate(Long productId)  {
        try {
            log.info("生成订单....");
            //调用orderInfoService对象在数据库中创建订单
            OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);

            //调用支付宝接口
            //创建支付宝请求对象
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            //创建具体请求参数对象，用于组装请求信息
            JSONObject bizContent = new JSONObject();
            //设置商户订单号
            bizContent.put("out_trade_no", orderInfo.getOrderNo());
            //设置订单总金额，由于订单金额单位为分，而参数中需要的是元，因此需要bigDecimal进行转换
            BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
            bizContent.put("total_amount", total);
            //设置订单标题
            bizContent.put("subject", orderInfo.getTitle());
            //设置支付产品码，比较固定（电脑支付场景下只支持一种类型）
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            //设置完成后，将bizContent具体请求对象转换成json并放置在请求中
            request.setBizContent(bizContent.toString());

            //利用alipay客户端执行请求
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            //判断请求是否成功
            if (response.isSuccess()){
                //打印响应信息主体
                log.info("调用成功====》{}",response.getBody());
            }else {
                log.info("调用失败====》{}，返回码"+response.getCode()+",返回描述为："+response.getMsg());
                throw new RuntimeException("创建支付交易失败.....");
            }
        return response.getBody();
        }catch (AlipayApiException e){
            throw new RuntimeException("创建支付交易失败.....");
        }
    }
}
