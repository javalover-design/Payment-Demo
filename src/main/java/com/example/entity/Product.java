package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lambda
 */
@Data
@TableName("t_product")
public class Product extends BaseEntity{
    /**
     * 产品名称
     */
    private  String title;
    /**
     * 产品价格
     */
    private  Integer price;

}
