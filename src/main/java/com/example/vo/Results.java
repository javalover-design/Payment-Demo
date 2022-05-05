package com.example.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lambda
 * 该类用于前后端交互，为前端设置一个标准的响应结果
 * 即该类设置了需要交给前端的数据
 */
@Data

public class Results {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 封装其他信息
     */
    private Map<String, Object> data =new HashMap<>();


    /**
     * 用于返回正确的结果显示
     * @return Results 表示返回数据对象
     */
    public static Results returnOk(){
        Results results = new Results();
        results.setCode(0);
        results.setMessage("Succeed!");
        return results;

    }

    /**
     * 返回错误的显示信息
     * @return Results
     */
    public static  Results returnError(){
        Results results = new Results();
        results.setCode(-1);
        results.setMessage("Failed");
        return results;
    }


    /**
     * 用于返回k-v的信息
     * @param  key 给前端传递的键
     * @param  value 给前端传递的值
     * @return Results
     */
    public Results returnData(String key,Object value){
        this.data.put(key, value);
        return this;
    }
}
