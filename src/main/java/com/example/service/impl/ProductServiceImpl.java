package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * @author lambda
 * ProductService接口的实现类，并且继承了 ServiceImpl<ProductMapper, Product>
 *  *  * 其中，第一个参数表示持久层接口，第二个表示实体类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
