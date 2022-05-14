package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.PaymentInfo;
import com.example.mapper.PaymentInfoMapper;
import com.example.service.PaymentInfoService;
import org.springframework.stereotype.Service;

/**
 * @author lambda
 * PaymentInfoService接口的实现类，并且继承了 ServiceImpl<PaymentInfoMapper, PaymentInfo>
 *  * 其中，第一个参数表示持久层接口，第二个表示实体类
 *
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {


}
