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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lambda
 * OrderInfoService接口的实现类，并且继承了 ServiceImpl<OrderInfoMapper, OrderInfo>
 * 其中，第一个参数表示持久层接口，第二个表示实体类
 *
 *
 */
@Service
@Slf4j
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
     * 创建查询对象，并设置查询条件，执行查询返回集合
     * @return
     */
    @Override
    public List<OrderInfo> listOrderByCreateTimeDesc() {
        //创建查询对象
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        //组装查询条件
        orderInfoQueryWrapper.orderByDesc("create_time");
        //使用持久层对象执行查询操作
        return orderInfoMapper.selectList(orderInfoQueryWrapper);
    }


    /**
     * 此方法用于根据订单编号来更新数据库中的订单状态
     * @param orderNo 订单编号
     * @param orderStatus 成功响应码
     */
    @Override
    public void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus) {
        log.info("更新数据库中的订单状态=======>"+orderStatus.getType());
        //创建一个查询条件，主要针对OrderInfo订单信息
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        //编写查询条件
        orderInfoQueryWrapper.eq("order_no",orderNo);
        //创建一个订单信息对象
        OrderInfo orderInfo = new OrderInfo();
        //设置要更新的订单状态
        orderInfo.setOrderStatus(orderStatus.getType());
        //执行更新操作
        orderInfoMapper.update(orderInfo,orderInfoQueryWrapper);


    }

    /**
     * 根据订单号获取订单状态
     * @param orderNo the order no
     * @return
     */
    @Override
    public String getOrderStatus(String orderNo) {
        //进行查询订单的操作
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        //构造查询条件
        orderInfoQueryWrapper.eq("order_no",orderNo);
        //根据订单号查询的订单信息必须是唯一的，因此使用selectOne
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoQueryWrapper);
        //判断订单信息是否为空，如果为空，直接将订单状态设置为null
        if (orderInfo==null){
            return null;
        }
        return orderInfo.getOrderStatus();
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
