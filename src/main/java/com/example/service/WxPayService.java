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
     *  核实订单状态
     * @param orderNo the order no
     * @throws IOException the io exception
     */
    void checkOrderStatus(String orderNo) throws IOException;
}
