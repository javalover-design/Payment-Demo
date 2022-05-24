package com.example.service;

/**
 * The interface Ali pay service.
 *
 * @author lambda
 */
public interface AliPayService {
    /**
     * Trade create string.
     *创建支付宝支付订单
     * @param productId the product id
     * @return the string
     */
    String tradeCreate(Long productId);
}
