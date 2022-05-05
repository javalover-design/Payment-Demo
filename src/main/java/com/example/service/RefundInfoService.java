package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.RefundInfo;

/**
 * @author lambda
 * 同样的RefundInfoService也不需要书写任何方法，所有的增删该查分页等操作都是由IService完成
 *  *   并且继承的IService接口的泛型是对应的实体类
 */

public interface RefundInfoService extends IService<RefundInfo> {

}
