package com.test.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code, T data, String message) {
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200,data,"request successfully");
    }

    public static <T> RestBean<T> unauthorized(String message){
        return failure(401,message);//failure是你自己定义的函数，在最下面
    }

    public static <T> RestBean<T> forbidden(String message){
        return failure(403,message);//failure是你自己定义的函数，在最下面
    }

    //返回无参数的success对象
    public static <T> RestBean<T> success(){
        return success(null);
    }

    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    // 自定义的失败的方法！！！
    public static <T> RestBean<T> failure(int code,String message){
        return new RestBean<>(code,null,message);

    }
}
