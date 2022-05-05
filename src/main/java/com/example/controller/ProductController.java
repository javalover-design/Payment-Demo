package com.example.controller;

import com.example.entity.Product;
import com.example.service.ProductService;
import com.example.vo.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author lambda
 */
@CrossOrigin //开放前端的跨域访问
@Api(tags = "商品管理")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @ApiOperation("测试接口")
    @GetMapping("/test")
    public Results test(){
        return Results.returnOk().returnData("message","支付成功！").returnData("now",new Date());

    }

    /**
     * 表示从数据库中获取数据并通过Results的方法返回到前端
     * @return Results
     */
    @ApiOperation("商品列表")
    @GetMapping("/list")
    public Results getProductList(){
        List<Product> list = productService.list();
        return Results.returnOk().returnData("productList",list);

    }
}
