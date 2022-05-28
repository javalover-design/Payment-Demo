package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.example.entity.OrderInfo;
import com.example.enums.OrderStatus;
import com.example.enums.PayType;
import com.example.service.AliPayService;
import com.example.service.OrderInfoService;
import com.example.service.PaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lambda
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private  AlipayClient alipayClient;

    @Resource
    private  Environment config;

    @Resource
    private PaymentInfoService paymentInfoService;

    /**
     * 添加可重入锁对象，进行数据的并发控制
     */
    private final ReentrantLock lock=new ReentrantLock();
    /**
     * 根据订单号创建订单并发起支付请求获取平台响应返回到前端
     * @param productId the product id
     * @return 返回支付请求调用的响应主体信息，返回到controller层
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String tradeCreate(Long productId)  {
        try {
            log.info("生成订单....");
            //调用orderInfoService对象在数据库中创建订单
            OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId, PayType.ALIPAY.getType());

            //调用支付宝接口
            //创建支付宝请求对象
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            //设置请求处理完成后的跳转的地址
            request.setReturnUrl(config.getProperty("alipay.return-url"));
            //设置支付宝异步通知的通知地址（需要进行内网穿透）
            request.setNotifyUrl(config.getProperty("alipay.notify-url"));
            //创建具体请求参数对象，用于组装请求信息
            JSONObject bizContent = new JSONObject();
            //设置商户订单号
            bizContent.put("out_trade_no", orderInfo.getOrderNo());
            //设置订单总金额，由于订单金额单位为分，而参数中需要的是元，因此需要bigDecimal进行转换
            BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
            bizContent.put("total_amount", total);
            //设置订单标题
            bizContent.put("subject", orderInfo.getTitle());
            //设置支付产品码，比较固定（电脑支付场景下只支持一种类型）
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            //设置完成后，将bizContent具体请求对象转换成json并放置在请求中
            request.setBizContent(bizContent.toString());

            //利用alipay客户端执行请求
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            //判断请求是否成功
            if (response.isSuccess()){
                //打印响应信息主体
                log.info("调用成功====》{}",response.getBody());
            }else {
                log.info("调用失败====》{}，返回码"+response.getCode()+",返回描述为："+response.getMsg());
                throw new RuntimeException("创建支付交易失败.....");
            }
        return response.getBody();
        }catch (AlipayApiException e){
            throw new RuntimeException("创建支付交易失败.....");
        }
    }

    /**
     * 商户系统订单处理
     * @param params 支付宝平台异步通知传递的参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processOrder(Map<String, String> params) {
        log.info("处理订单.......");
        //获取传递信息中的订单号
        String orderNo = params.get("out_trade_no");


        /**
         * 在对业务数据进行状态检查之前需要利用数据锁进行处理，进行并发控制
         * 避免数据重入造成混乱，
         * 此处使用尝试获取锁的判断，如果没有获取锁，此时则返回false，直接进行下面的操作
         * 不会等待锁释放，造成阻塞。
         */
        if (lock.tryLock()) {
            try {
                //接口调用幂等性问题：在更新订单状态，记录支付日志之前过滤重复通知（无论接口被调用多少次，以下只执行一次）
                //首先获取订单状态
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                if (!OrderStatus.NOTPAY.getType().equals(orderStatus)){
                    //如果订单状态不是未支付，则直接返回，不需要任何处理
                    return;
                }
                //更新订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
                //记录支付日志
                paymentInfoService.createPaymentInfoForAliPay(params);
            }finally {
                //必须要主动释放锁
                lock.unlock();
            }
        }

    }

    /**
     * 用户取消订单方法编写
     * @param orderNo 订单号
     */
    @Override
    public void cancelOrder(String orderNo) {

        //调用支付统一收单交易关闭接口
        this.closeOrder(orderNo);

        //更新用户的订单状态
        orderInfoService.updateStatusByOrderNo(orderNo,OrderStatus.CANCEL);

    }

    /**
     * 商户查询订单信息
     * @param orderNo 订单号
     * @return 返回订单查询结果，如果返回为null，说明支付宝端没有创建订单
     */
    @Override
    public String queryOrder(String orderNo) {

        try {

            log.info("查单接口调用----》{}", orderNo);
            //首先创建交易查询对象
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            //组装请求参数对象（向支付宝端查单需要提供哪些参数）
            JSONObject bizContent = new JSONObject();
            //组装订单号
            bizContent.put("out_trade_no", orderNo);
            request.setBizContent(bizContent.toString());
            //执行查询请求
            AlipayTradeQueryResponse response= alipayClient.execute(request);
            if (response.isSuccess()){
                log.info("调用成功，返回结果---》{}",response.getBody());
                return response.getBody();
            }else{
                log.info("调用失败，返回响应码"+response.getCode()+",响应结果为"+response.getBody());
               // throw new RuntimeException("响应失败....");
                //调用失败直接返回为null
                return null;
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("查询订单接口调用失败.....");
        }
    }

    /**
     * 关单接口调用
     * @param orderNo 订单号
     */
    private void closeOrder(String orderNo) {
        try {


            log.info("关单接口调用，订单号---》{}", orderNo);
            //创建关单请求
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            //创建请求参数对象
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderNo);
            //将对应的参数设置到请求对象中
            request.setBizContent(bizContent.toString());
            //使用支付客户端对象执行请求
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            //判断请求是否成功
            if (response.isSuccess()){
                //打印响应信息主体
                log.info("调用成功====》{}",response.getBody());
            }else {
                log.info("调用失败====》{}，返回码"+response.getCode()+",返回描述为："+response.getMsg());
               // throw new RuntimeException("关单接口调用失败....."); 让其正常结束
            }

        } catch (AlipayApiException e) {
            throw new RuntimeException("关单接口调用出现异常");
        }


    }
}
