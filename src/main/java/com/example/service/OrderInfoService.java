package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.OrderInfo;

import java.util.List;

/**
 * The interface Order info service.
 *
 * @author lambda  同样的OrderInfoService也不需要书写任何方法，所有的增删该查分页等操作都是由IService完成 并且继承的IService接口的泛型是对应的实体类
 */
public interface OrderInfoService  extends IService<OrderInfo> {

    /**
     * Create order by product id order info.
     * 根据产品的id生成对应的订单信息
     *
     * @param productId the product id
     * @return the order info
     */
    OrderInfo createOrderByProductId(Long productId);

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
     *按创建时间降序排序订单信息
     * @return the list
     */
    List<OrderInfo> listOrderByCreateTimeDesc();
}
