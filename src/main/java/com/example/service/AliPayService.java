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

    /**
     * Query order string.
     * 商户向支付宝端查询订单结果
     *
     * @param orderNo the order no
     * @return the string
     */
    String queryOrder(String orderNo);

    /**
     * Check order status.
     * 检查订单状态
     *
     * @param orderNo the order no
     */
    void checkOrderStatus(String orderNo);

    /**
     * Refund.
     * 退款操作
     *
     * @param orderNo the order no
     * @param reason  the reason
     */
    void refund(String orderNo, String reason);

    /**
     * Query refund string.
     * 查询退款结果
     *
     * @param orderNo the order no
     * @return the string
     */
    String queryRefund(String orderNo);

    /**
     * Query bill string.
     *  查询订单下载地址
     * @param billDate the bill date
     * @param type     the type
     * @return the string
     */
    String queryBill(String billDate, String type);
}
