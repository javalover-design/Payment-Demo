package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lambda
 */
@Data
@TableName("t_payment_info")
public class PaymentInfo extends BaseEntity{
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 交易系统支付编号
     */
    private  String transactionId;
    /**
     * 支付类型
     */
    private String paymentType;
    /**
     * 交易类型
     */
    private  String tradeType;
    /**
     * 交易状态
     */
    private String tradeState;
    /**
     * 支付金额
     */
    private Integer payerTotal;
    /**
     * 通知参数
     */
    private  String content;


}
