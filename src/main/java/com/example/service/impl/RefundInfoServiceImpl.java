package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.RefundInfo;
import com.example.mapper.RefundInfoMapper;
import com.example.service.OrderInfoService;
import com.example.service.RefundInfoService;
import com.example.util.OrderNoUtils;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lambda
 * RefundInfoService接口的实现类，并且继承了 ServiceImpl<RefundInfoMapper, RefundInfo>
 *  *  * 其中，第一个参数表示持久层接口，第二个表示实体类
 *
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Resource
    private OrderInfoService orderInfoService;
    @Resource
    private RefundInfoMapper refundInfoMapper;

    /**
     *
     * @param orderNo 订单编号
     * @param reason 退款原因
     * @return RefundInfo 退款单信息
     */
    @Override
    public RefundInfo createRefundByOrderNo(String orderNo, String reason) {
        //根据订单号处理订单信息
        OrderInfo orderInfo=orderInfoService.getOrderByOrderNo(orderNo);

        //根据订单号生成退款单记录
        RefundInfo refundInfo = new RefundInfo();
        //订单编号
        refundInfo.setOrderNo(orderNo);
        //退款单编号
        refundInfo.setRefundNo(OrderNoUtils.getRefundNo());
        //原来订单金额
        refundInfo.setTotalFee(orderInfo.getTotalFee());
        //退款金额
        refundInfo.setRefund(orderInfo.getTotalFee());
        //退款原因
        refundInfo.setReason(reason);
        //将退款信息插入数据库
        refundInfoMapper.insert(refundInfo);
        return refundInfo;
    }

    @Override
    public void updateRefund(String content) {
        //将退款请求响应的返回对象转成Map类型信息
        Gson gson = new Gson();
        Map<String, String> resultMap = gson.fromJson(content, HashMap.class);
        //根据退款单编号，修改退款单
        QueryWrapper<RefundInfo> refundInfoQueryWrapper = new QueryWrapper<>();
        refundInfoQueryWrapper.eq("refund_no",resultMap.get("out_refund_no"));

        //设置要修改的字段
        RefundInfo refundInfo = new RefundInfo();
        //微信支付退款单号
        refundInfo.setRefundId(resultMap.get("refund_id"));

        //查询申请退款和退款中的返回参数(退款中)
        if (resultMap.get("status")!=null){
            //设置退款状态
            refundInfo.setRefundStatus(resultMap.get("status"));
            //将全部响应结果存入数据库的content字段中
            refundInfo.setContentReturn(content);
        }

        //退款回调中的回调参数(这是退款之后的状态)
        if (resultMap.get("refund_status")!=null){
                refundInfo.setRefundStatus(resultMap.get("refund-status"));
            //将全部响应结果存入数据库的content字段中
            refundInfo.setContentNotify(content);
        }

        //更新退款单
        refundInfoMapper.update(refundInfo,refundInfoQueryWrapper);

    }
}
