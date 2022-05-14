package com.example.controller;

import com.example.config.WxPayConfig;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lambda
 *
 */
@Api("测试接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

         @Resource
        private WxPayConfig wxPayConfig;

         @ApiOperation("测试获取信息")
         @GetMapping
         public Results testProperties(){
             String mchId = wxPayConfig.getMchId();
             return Results.returnOk().returnData("商户号",mchId);

         }


}
