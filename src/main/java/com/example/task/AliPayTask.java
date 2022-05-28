package com.example.task;
import com.example.entity.OrderInfo;
import com.example.enums.PayType;
import com.example.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
/**
 * @author lambda
 */
@Slf4j
@Component
public class AliPayTask {
    @Resource
    private OrderInfoService orderInfoService;

    /**
     * 每30秒查询一次订单信息，查询创建1分钟并且未支付的订单
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm(){
        log.info("定时查询订单任务启动");
        //调用查询未支付订单的方法获取所有的订单信息
        List<OrderInfo> noPayOrderList = orderInfoService.getNoPayOrderByDuration(1, PayType.ALIPAY.getType());

        //遍历超时订单
        for (OrderInfo orderInfo : noPayOrderList) {
            String orderNo = orderInfo.getOrderNo();
            log.info("超时1分钟未支付的订单---》{}",orderNo);
        }

    }
}
