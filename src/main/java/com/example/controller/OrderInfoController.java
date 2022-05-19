package com.example.controller;
import com.example.entity.OrderInfo;
import com.example.enums.OrderStatus;
import com.example.service.OrderInfoService;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lambda
 *
 */
@CrossOrigin
@Api(tags = "商品订单管理")
@RestController
@RequestMapping("/api/order-info")
public class OrderInfoController {

    /**注入orderInfoService*/
   @Resource
   private OrderInfoService orderInfoService;

    /**
     * 获取所有的订单信息
     * @return
     */
   @GetMapping("/list")
    public Results list(){
     List<OrderInfo> orderInfoList= orderInfoService.listOrderByCreateTimeDesc();
     return Results.returnOk().returnData("list",orderInfoList);
   }


    /**
     * 查询本地订单状态
     * @param orderNo
     * @return
     */
   @GetMapping("query-order-status/{orderNo}")
   public Results queryOrderStatus(@PathVariable String orderNo){
       String orderStatus = orderInfoService.getOrderStatus(orderNo);
    //判断获取到的信息是否为空
       if (OrderStatus.SUCCESS.getType().equals(orderStatus)){
           //表示订单信息已经支付，返回成功
           return Results.returnOk().setMessage("支付成功！");
       }

       //否则携带信息提示支付中,返回101状态码
       return Results.returnOk().setMessage("支付中.......").setCode(101);

   }
}
