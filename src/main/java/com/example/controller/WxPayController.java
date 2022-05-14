package com.example.controller;

import com.example.service.WxPayService;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lambda
 *
 */
@CrossOrigin //跨域注解
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags = "网站微信支付API")
@Slf4j //日志对象
public class WxPayController {

    /**
     * 由于微信支付controller中需要调用微信支付服务，所以需要注入
     */
    @Resource
    private WxPayService wxPayService;

    /**
     * 该方法从前端获取产品的id，之后调用支付接口
     * @param productId
     * @return
     */
    @ApiOperation("调用统一支付接口，生成二维码")
    @PostMapping("native/{productId}")
    public Results nativePay(@PathVariable Long productId) throws Exception {
        log.info("发起支付请求.......");

        //调用支付方法后，返回微信二维码链接以及订单号
        Map<String,Object> map=wxPayService.nativePay(productId);

        //Results results = Results.returnOk();
        ////设置Data封装信息，由于set方法没有返回值，所以先创建Results对象，最后返回
        //results.setData(map);
        //return results;
        //或者可以进行链式操作
        return Results.returnOk().setData(map);
    }
}
