package com.example.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.entity.OrderInfo;
import com.example.service.AliPayService;
import com.example.service.OrderInfoService;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

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

    @Resource
    private Environment config;

    @Resource
    private OrderInfoService orderInfoService;
    /**
     * 发起支付通知请求接口
     * @param productId 产品id
     * @return 返回一个字符格式的form表单，通过请求路径/trade/page/pay/{productId}交由前端渲染
     */
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

    /**
     * 支付宝异步通知处理结果
     * @param params 支付宝异步通知发过来的参数
     * @return 最终返回商户程序给予支付宝平台的信息
     */
    @ApiOperation("支付通知")
    @PostMapping("/trade/notify")
    public String tradeNotify(@RequestParam Map<String,String> params)  {
        try {
            //@RequestParam表示将参数从请求中取出放入map集合中
            log.info("支付通知正在执行");
            log.info("通知参数----》{}", params);
            //result表示商家需要给支付宝反馈的异步通知结果（success表示成功，需要后续的业务来规定是否为
            // success）
            String result = "failure";

            //异步通知验签（使用我们引入的支付宝SDK验证签名）
            //一个是异步通知的结果参数，一个是支付宝的公钥，一个是字符集，一个是加密方式，得到一个布尔值结果
            boolean signVerified = AlipaySignature.rsaCheckV1(params, config.getProperty("alipay.alipay-public-key"),
                    AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);

            //对签名结果进行判断
            if (!signVerified) {
                //TODO:验签失败则记录异常日志，并在response中返回failure
                log.error("支付成功，异步通知验签失败......");
                return result;
            }

            //TODO:验证成功，按照支付结果异步通知中的描述，
            // 对支付结果中的业务内容进行二次校验，
            // 校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure


            //1.商户需要验证该通知数据中的 out_trade_no 是否为商户系统中创建的订单号；
            //获取对应的订单号
            String outTradeNo = params.get("out_trade_no");
            //利用获取的订单号查询对应的订单信息(返回一个订单对象)
            OrderInfo order = orderInfoService.getOrderByOrderNo(outTradeNo);
            //对订单对象进行判断
            if (order==null){
                log.error("订单不存在......");
                return result;
            }

            //2.判断 total_amount 是否确实为该订单的实际金额（即商户订单创建时的金额）
            //从参数中获取金额(单位为元)，但是数据库中的单位为分，因此需要进行转换
            String totalAmount = params.get("total_amount");
            int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();
            //获取订单中的金额
            int totalFeeInt = order.getTotalFee();
            if (totalFeeInt!=totalAmountInt){
                //如果不等，则说明金额不对
                log.error("金额校验失败");
                return result;
            }

            //3.校验通知中的 seller_id(对应商户的PID)（或者 seller_email) 是否为 out_trade_no
            // 这笔单据的对应的操作方（有的时候，一个商户可能有多个 seller_id/seller_email）
            String sellerId = params.get("seller_id");
            //获取实际的商户PID
            String pid = config.getProperty("alipay.seller-id");
            if (!sellerId.equals(pid)){
                //用商户的PID与参数中的sellerID进行比较
                log.error("商家PID校验失败....");
                return result;
            }

            //4.验证 app_id 是否为该商户本身
            String appId = params.get("app_id");
            String appIdProperty = config.getProperty("alipay.app-id");
            if (!appId.equals(appIdProperty)){
                log.error("appId校验失败");
                return result;
            }

            //在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS
            // 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功。
            //获取交易状态
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus)){
                //如果不满足交易状态成功参数，则直接返回failure
                log.error("支付未成功....");
                return result;
            }
            //以上4步校验成功后设置为success，之后返回结果，商户可以自身进行后续的处理
            //商户处理自身业务
            aliPayService.processOrder(params);

            result = "success";

            log.info("支付成功，异步通知验签成功.......");
            return result;
        }catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException("异步通知验证签名出现异常....");
        }



    }



}
