package com.example.controller;

import com.example.service.AliPayService;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author lambda
 */
@CrossOrigin
@RestController
@RequestMapping("/api/ali-pay")
@Api(tags = "网站支付宝支付")
@Slf4j
public class AliPayController {

    @Resource
    private AliPayService aliPayService;

    @ApiOperation("统一收单下单并支付页面接口")
    @PostMapping("/trade/page/pay/{productId}")
    public Results tradePagePay(@PathVariable Long productId){
        log.info("统一收单下单并支付页面接口");
        //发起交易请求后，会返回一个form表单格式的字符串（要有前端渲染）
        String formStr=aliPayService.tradeCreate(productId);
        //最后需要将支付宝的响应信息传递给前端,到前端之后会自动提交表单到action指定的支付宝开放平台中
        //从而为用户展示支付页面。
        return Results.returnOk().returnData("formStr",formStr);

    }

}
