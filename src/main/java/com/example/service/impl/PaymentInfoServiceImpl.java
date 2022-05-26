package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.PaymentInfo;
import com.example.enums.PayType;
import com.example.mapper.PaymentInfoMapper;
import com.example.service.PaymentInfoService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lambda
 * PaymentInfoService接口的实现类，并且继承了 ServiceImpl<PaymentInfoMapper, PaymentInfo>
 *  * 其中，第一个参数表示持久层接口，第二个表示实体类
 *
 */
@Service
@Slf4j
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;
    /**
     * 创建订单日志方法
     * @param plainText the plain text
     */
    @Override
    public void createPaymentInfo(String plainText) {
        log.info("记录支付日志....");
        //将明文数据转换成map形式的数据
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        //获取订单号
        String orderNo = (String)plainTextMap.get("out_trade_no");
        //从明文中获取transactionId
        String transactionId=(String)plainTextMap.get("transaction_id");
        //从明文中获取交易类型
        String tradeType=(String)plainTextMap.get("trade_type");
        //从明文中获取交易状态
        String tradeState=(String)plainTextMap.get("trade_state");
        //从明文中获取支付的金额，需要从amount中获取实际支付金额
        Map<String, Object> amountMap=(Map)plainTextMap.get("amount");
        //由于直接获取的是double类型，但是需要的是int，所以需要将其转成int
        Integer payerTotal = ((Double) amountMap.get("payer_total")).intValue();
        //首先获取请求信息中
        //创建支付日志对象
        PaymentInfo paymentInfo = new PaymentInfo();
        //设置支付订单号
        paymentInfo.setOrderNo(orderNo);
        //设置支付类型(通过枚举类型)
        paymentInfo.setPaymentType(PayType.WXPAY.getType());
        //设置支付业务编号
        paymentInfo.setTransactionId(transactionId);
        //设置支付的交易类型（比如native支付，小程序支付等，从明文中获取信息）
        paymentInfo.setTradeType(tradeType);
        //设置支付的交易状态
        paymentInfo.setTradeState(tradeState);
        //设置实际支付的金额
        paymentInfo.setPayerTotal(payerTotal);
        //同时将微信返回的支付信息作为一个整体存入数据库中
        paymentInfo.setContent(plainText);
        //最后将信息插入数据库中
        paymentInfoMapper.insert(paymentInfo);


    }

    /**
     * 记录支付宝的支付日志
     * @param params the params 支付通知参数
     */
    @Override
    public void createPaymentInfoForAliPay(Map<String, String> params) {
        log.info("记录支付宝支付日志.....");
        //创建支付信息对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(params.get("out_trade_no"));
        paymentInfo.setPaymentType(PayType.ALIPAY.getType());
        //设置业务编号(支付宝对应的是trade_no)
        paymentInfo.setTransactionId(params.get("trade_no"));
        //设置支付的场景
        paymentInfo.setTradeType("电脑网站支付");
        //设置交易状态
        paymentInfo.setTradeState(params.get("trade_status"));
        //设置交易金额，此处依旧需要转换(支付宝端对应的是元，数据库中对应分)
        int totalAmount=new BigDecimal(params.get("total_amount")).multiply(new BigDecimal("100")).intValue();
        paymentInfo.setPayerTotal(totalAmount);

        //之后设置备注信息，需要将平台传入的map集合信息转成字符串类型存入数据库
        Gson gson = new Gson();
        String content = gson.toJson(params, HashMap.class);
        paymentInfo.setContent(content);
        //将信息插入数据库中
        paymentInfoMapper.insert(paymentInfo);


    }
}
