package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lambda
 * 需要注意的是接口继承了BaseMapper，并且泛型使用PaymentInfoMapper所对应的实体类
 * 并且由于BaseMapper自动实现了增删改查的方法，所以不需要手动书写任何的sql
 */
@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}
