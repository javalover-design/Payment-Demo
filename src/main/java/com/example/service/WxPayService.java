package com.example.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * The interface Wx pay service.
 *
 * @author lambda
 */
public interface WxPayService {

    /**
     * Native pay map.
     * Native支付方法，生成二维码url和其他信息
     *
     * @param productId the product id
     * @return the map
     * @throws Exception the exception
     */
    Map<String, Object> nativePay(Long productId) throws Exception;

    /**
     * Process order.
     * 处理订单
     *
     * @param bodyMap the body map
     * @throws GeneralSecurityException the general security exception
     */
    void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException;

    /**
     * Cancel order.
     * 取消订单
     *
     * @param orderNo the order no
     * @throws IOException the io exception
     */
    void cancelOrder(String orderNo) throws IOException;

    /**
     * Query order string.
     * 查询订单方法
     *
     * @param orderNo the order no
     * @return the string
     * @throws IOException the io exception
     */
    String queryOrder(String orderNo) throws IOException;

    /**
     * Check order status.
     * 核实订单状态
     *
     * @param orderNo the order no
     * @throws IOException the io exception
     */
    void checkOrderStatus(String orderNo) throws IOException;

    /**
     * Refund.
     * 退款方法
     *
     * @param orderNo the order no
     * @param reason  the reason
     * @throws IOException the io exception
     */
    void refund(String orderNo, String reason) throws IOException;

    /**
     * Query refund string.
     * 查询退款结果
     *
     * @param refundNo the refund no
     * @return the string
     * @throws IOException the io exception
     */
    String queryRefund(String refundNo) throws IOException;

    /**
     * Process refund.
     *  处理退款订单
     * @param bodyMap the body map
     * @throws GeneralSecurityException the general security exception
     */
    void processRefund(Map<String, Object> bodyMap) throws GeneralSecurityException;

    /**
     * Query bill string.
     *查询账单，返回url地址
     * @param billDate the bill date
     * @param type     the type
     * @return the string
     * @throws IOException the io exception
     */
    String queryBill(String billDate, String type) throws IOException;

    String downloadBill(String billDate, String type) throws IOException;
}
