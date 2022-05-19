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
     * @param orderNo the order no
     */
    void cancelOrder(String orderNo) throws IOException;
}
