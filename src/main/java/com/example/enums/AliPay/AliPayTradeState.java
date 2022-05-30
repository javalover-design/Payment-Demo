package com.example.enums.AliPay;

/**
 * @author lambda
 */

public enum AliPayTradeState {
    /**
     * 支付成功
     */
    SUCCESS("TRADE_SUCCESS"),
    /**
     * 未支付
     */
    NOTPAY("WAIT_BUYER_PAY"),
    /**
     * 订单关闭
     */
    CLOSED("TRADE_SUCCESS"),
    /**
     * 退款成功
     */
    REFUND_SUCCESS("REFUND_SUCCESS"),

    /**
     * 退款失败
     */
    REFUND_ERROR("REFUND_ERROR");


    private final String type;

   private AliPayTradeState(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
