package com.example.controller;

import com.example.enums.StatusCode;
import com.example.service.WxPayService;
import com.example.util.HttpUtils;
import com.example.util.WechatPay2ValidatorForRequest;
import com.example.vo.Results;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
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
     * 注入签名验证器
     */
    @Autowired
    @Qualifier("getScheduledUpdateCertificatesVerifier")
    private Verifier verifier;

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

    /**
     *
     * @param request 微信服务器发送的请求
     * @param response 返回给微信服务器的响应
     * @return  返回响应的结果（以JSON字符串格式返回）
     * 请求通知的地址notifyUrl
     * 微信官方要求通知使用POST方式接收
     */
    @PostMapping("/native/notify")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        //设置客户应答微信平台的应答对象
        Map<String, String> responseMap=new HashMap<>();
        //处理通知参数(使用工具类直接从请求中获取内容)
        String body = HttpUtils.readData(request);
        //将获取到的json数据转换成map
        Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
        //获取请求通知的请求id
        String requestId=(String)bodyMap.get("id");
        log.info("支付通知的id===>{}",bodyMap.get("id"));
        //{}表示占位符，后面的值会替换占位符
        log.info("支付通知的完整数据===>{}",body);

        //TODO:签名验证（针对微信异步请求）

        WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest = new WechatPay2ValidatorForRequest(verifier, requestId,body);
        //验证签名
        if(!wechatPay2ValidatorForRequest.validate(request)){
            //控制台输出
            log.info("通知验证签名失败");
            response.setStatus(StatusCode.SIGN_ERROR.getCode());
            responseMap.put("code",StatusCode.SIGN_ERROR.getDescription());
            responseMap.put("message",StatusCode.SIGN_ERROR.getDescription());
            return gson.toJson(responseMap);
        }

        //TODO：订单处理
        wxPayService.processOrder(bodyMap);

        //返回成功的应答
        response.setStatus(StatusCode.SUCCESS.getCode());
        responseMap.put("code","SUCCESS");
        responseMap.put("message","成功");
        //客户成功应答的信息转换成JSON数据传递给微信支付平台
        return gson.toJson(responseMap);

    }
}
