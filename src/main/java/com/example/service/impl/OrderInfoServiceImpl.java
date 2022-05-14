package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.Product;
import com.example.enums.OrderStatus;
import com.example.mapper.OrderInfoMapper;
import com.example.mapper.ProductMapper;
import com.example.service.OrderInfoService;
import com.example.util.OrderNoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lambda
 * OrderInfoService接口的实现类，并且继承了 ServiceImpl<OrderInfoMapper, OrderInfo>
 * 其中，第一个参数表示持久层接口，第二个表示实体类
 *
 *
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Override
    public OrderInfo createOrderByProductId(Long productId) {

        //查找已存在，但是并未支付的订单信息
        OrderInfo orderInfoNoPay = getNoPayOrderByProductId(productId);
        if (orderInfoNoPay!=null){
            return orderInfoNoPay;
        }
        //获取商品的对象
        Product product = productMapper.selectById(productId);

        //生成订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTitle(product.getTitle());
        //订单号
        orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo.setTotalFee(product.getPrice());
        orderInfo.setProductId(productId);
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());

        //将订单信息存入数据库
        orderInfoMapper.insert(orderInfo);

        return orderInfo;
    }

    /**
     * 存储订单二维码
     * @param orderNo the order no
     * @param codeUrl the code url
     */
    @Override
    public void saveCodeUrl(String orderNo, String codeUrl) {
        //创建一个查询条件
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        //查询的where条件
        orderInfoQueryWrapper.eq("order_no",orderNo);
        //即将修改的字段
        OrderInfo orderInfo = new OrderInfo();
        orderInfoMapper.update(orderInfo,orderInfoQueryWrapper);


    }

    /**
     * 该方法用于获取用户未支付的订单(由于只在该类中使用，所以定义为私有方法)
     * @param productId
     * @return
     */
    private  OrderInfo getNoPayOrderByProductId(Long productId){
        //使用MyBatis-plus的查询器
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        //设置判断条件，id和类型信息
         orderInfoQueryWrapper.eq("product_id", productId);
         orderInfoQueryWrapper.eq("order_status",OrderStatus.NOTPAY.getType());

         //使用自带的selectOne方法判断是否同时满足条件
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoQueryWrapper);
        return orderInfo;

    }
}
