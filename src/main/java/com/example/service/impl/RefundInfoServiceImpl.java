package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.RefundInfo;
import com.example.mapper.RefundInfoMapper;
import com.example.service.RefundInfoService;
import org.springframework.stereotype.Service;

/**
 * @author lambda
 * RefundInfoService接口的实现类，并且继承了 ServiceImpl<RefundInfoMapper, RefundInfo>
 *  *  * 其中，第一个参数表示持久层接口，第二个表示实体类
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
}
