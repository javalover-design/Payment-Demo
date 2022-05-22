package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lambda
 *
 */
@AllArgsConstructor
@Getter
public enum StatusCode {

    /**
     * 处理成功
     */
    SUCCESS(200,"处理成功"),
    /**
     * 处理成功，但是没有返回体
     */
    SUCCESS_NOT_BODY(204,"处理成功，但是没有返回体"),
    /**
     * 参数错误
     */
    PARAM_ERROR(400,"参数错误"),
    /**
     * 系统异常
     */
    SYSTEMERROR(500,"系统错误"),

    /**
     * 签名错误
     */
    SIGN_ERROR(401,"签名错误"),

    /**
     * 业务规则限制
     */
    RULELIMIT(403,"业务规则限制"),

    /**
     * 商户订单号重复
     */
    OUT_TRADE_NO_USED(403,"商户订单号重复"),
    /**
     * 订单不存在
     */
    ORDERNOTEXIST(404,"订单不存在"),

    /**
     * 订单已关闭
     */
    ORDER_CLOSED(400,"订单已关闭"),

    /**
     * openid和appid不匹配
     */
    OPENID_MISMATCH(500,"openid和appid不匹配"),

    /**
     * 余额不足
     */
    NOTENOUGH(403,"余额不足"),

    /**
     * 商户无权限
     */
    NOAUTH(403,"商户无权限"),

    /**
     * 商户号不存在
     */
    MCH_NOT_EXISTS(400,"商户号不存在"),

    /**
     * 订单号非法
     */
    INVALID_TRANSACTIONID(500,"订单号非法"),

    /**
     * 无效请求
     */
    INVALID_REQUEST(400,"无效请求"),

    /**
     * 频率超限
     */
    FREQUENCY_LIMITED(429,"频率超限"),

    /**
     * 银行系统异常
     */
    BANKERROR(500,"银行系统异常"),

    /**
     * appid和mch_id不匹配
     */
    APPID_MCHID_NOT_MATCH(400,"appid和mch_id不匹配"),

    /**
     * 帐号异常
     */
    ACCOUNTERROR(403,"账号异常"),

    /**
     * 验证签名失败
     */
    FAILED_VERIFY_SIGNATURE(500,"验证签名失败");

    private final Integer code;
    private final String description;

}
