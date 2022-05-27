package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lambda
 *
 */
@Data
@TableName("t_order_info")  //表示指定表名
public class OrderInfo extends BaseEntity{

    /**
     * 订单标题
     */
    private  String title;
    /**
     * 订单编号
     */
    private  String orderNo;
    /**
     * 用户ID
     */
    private  Long userId;
    /**
     * 产品ID
      */
    private  Long productId;
    /**
     * 订单金额
     */
    private  Integer totalFee;
    /**
     * 订单二维码链接
     */
    private  String codeUrl;
    /**
     * 订单状态
     */
    private String orderStatus;
    /**
     * 支付类型
     */
    private String paymentType;



}
