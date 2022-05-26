package com.example.service;

import java.util.Map;

/**
 * The interface Payment info service.
 *
 * @author lambda
 */
public interface PaymentInfoService {
    /**
     * Create payment info.
     *
     * @param plainText the plain text
     */
    void createPaymentInfo(String plainText);

    /**
     * Create payment info for ali pay.
     *为支付创建日志记录
     * @param params the params 支付通知参数
     */
    void createPaymentInfoForAliPay(Map<String, String> params);

}
