package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.OrderInfo;
import com.example.enums.OrderStatus;

import java.util.List;

/**
 * The interface Order info service.
 *
 * @author lambda 同样的OrderInfoService也不需要书写任何方法，所有的增删该查分页等操作都是由IService完成 并且继承的IService接口的泛型是对应的实体类
 */
public interface OrderInfoService  extends IService<OrderInfo> {

    /**
     * Create order by product id order info.
     * 根据产品的id生成对应的订单信息
     *
     * @param productId   the product id
     * @param paymentType the payment type
     * @return the order info
     */
    OrderInfo createOrderByProductId(Long productId,String paymentType);

    /**
     * Save code url.
     * 保存二维码的方法
     *
     * @param orderNo the order no
     * @param codeUrl the code url
     */
    void saveCodeUrl(String orderNo,String codeUrl);

    /**
     * List order by create time desc list.
     * 按创建时间降序排序订单信息
     *
     * @return the list
     */
    List<OrderInfo> listOrderByCreateTimeDesc();

    /**
     * Update status by order no.
     * 根据订单号更新数据库中的订单状态
     *
     * @param orderNo     the order no
     * @param orderStatus the order status
     */
    void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);

    /**
     * Gets order status.
     * 获取订单状态
     *
     * @param orderNo the order no
     * @return the order status
     */
    String getOrderStatus(String orderNo);

    /**
     * Gets no pay order by duration.
     * 查询超过指定时间未支付的订单
     *
     * @param minutes     the
     * @param paymentType the payment type
     * @return the no pay order by duration
     */
    List<OrderInfo> getNoPayOrderByDuration(int minutes,String paymentType);

    /**
     * Gets order by order no.
     * 根据订单号获取订单
     *
     * @param orderNo the order no
     * @return the order by order no
     */
    OrderInfo getOrderByOrderNo(String orderNo);

}
