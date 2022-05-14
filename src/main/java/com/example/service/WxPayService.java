package com.example.service;

import java.util.Map;

/**
 * The interface Wx pay service.
 *
 * @author lambda
 *
 */
public interface WxPayService {

    /**
     * Native pay map.
     *  Native支付方法，生成二维码url和其他信息
     * @param productId the product id
     * @return the map
     * @throws Exception the exception
     */
    Map<String, Object> nativePay(Long productId) throws Exception;
}
