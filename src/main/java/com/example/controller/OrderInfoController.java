package com.example.controller;
import com.example.entity.OrderInfo;
import com.example.service.OrderInfoService;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

   @GetMapping("/list")
    public Results list(){
     List<OrderInfo> orderInfoList= orderInfoService.listOrderByCreateTimeDesc();
     return Results.returnOk().returnData("list",orderInfoList);
   }

}
