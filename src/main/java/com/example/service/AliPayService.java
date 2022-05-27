package com.example.service;

import java.util.Map;

/**
 * The interface Ali pay service.
 *
 * @author lambda
 */
public interface AliPayService {
    /**
     * Trade create string.
     * 创建支付宝支付订单
     *
     * @param productId the product id
     * @return the string
     */
    String tradeCreate(Long productId);

    /**
     * Process order.
     * 订单处理方法
     *
     * @param params the params
     */
    void processOrder(Map<String, String> params);

    /**
     * Cancel order.
     * 根据订单号取消订单
     *
     * @param orderNo the order no
     */
    void cancelOrder(String orderNo);
}
