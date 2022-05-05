package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.mapper.OrderInfoMapper;
import com.example.service.OrderInfoService;
import org.springframework.stereotype.Service;

/**
 * @author lambda
 * OrderInfoService接口的实现类，并且继承了 ServiceImpl<OrderInfoMapper, OrderInfo>
 * 其中，第一个参数表示持久层接口，第二个表示实体类
 *
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

}
