package com.example.task;

import com.example.entity.OrderInfo;
import com.example.enums.PayType;
import com.example.service.OrderInfoService;
import com.example.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author lambda
 */
@Slf4j
@Component
public class WxPayTask {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private WxPayService wxPayService;


    /**
     *编写一个定时查单的定时任务
     * 从第0秒开始，每隔30秒执行一次
     */
   // @Scheduled(cron = "0/30 * * * * ?")
    public  void orderConfirm() throws IOException {
            log.info("查询订单任务启动.......");
            //查询未支付并且时间超过五分钟的订单
        List<OrderInfo> noPayOrderList=orderInfoService.getNoPayOrderByDuration(5, PayType.WXPAY.getType());
        for (OrderInfo orderInfo : noPayOrderList) {
            String orderNo = orderInfo.getOrderNo();
            log.warn("超时订单---》{}",orderNo);
            //查询订单状态,调用微信支付查询订单接口，如果用户在微信端显示支付，用户处显示未支付，则需要修改订单状态，如果确实微信处显示未支付
            //则进行关单操作
            wxPayService.checkOrderStatus(orderNo);



        }
    }

}
