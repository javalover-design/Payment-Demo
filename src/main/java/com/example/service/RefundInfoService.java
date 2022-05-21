package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.RefundInfo;

/**
 * The interface Refund info service.
 *
 * @author lambda 同样的RefundInfoService也不需要书写任何方法，所有的增删该查分页等操作都是由IService完成  *   并且继承的IService接口的泛型是对应的实体类
 */
public interface RefundInfoService extends IService<RefundInfo> {

    /**
     * Create refund by order no refund info.
     * 根据订单号创建退款订单
     *
     * @param orderNo the order no
     * @param reason  the reason
     * @return the refund info
     */
    RefundInfo createRefundByOrderNo(String orderNo, String reason);

    /**
     * Update refund.
     *更新退款信息
     * @param bodyAsString the body as string
     */
    void updateRefund(String bodyAsString);
}
