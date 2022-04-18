package com.xxx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回枚举类
 * Created by 彭天怡 2022/4/13.
 */
@Getter
@AllArgsConstructor
public enum RespBeanEnum {

    //成功
    SUCCESS(200,"SUCCESS"),
    //错误
    Error(500,"服务端异常"),

    LOGIN_ERROR(500210,"用户名或密码不正确"),

    MOBILE_ERROR(500310,"手机号码不正确"),
    REPATE_ERROR(500501,"该商品每人限购一件"),

    SESSION_ERROR(500502,"用户不存在"),

    ORDER_NOT_EXIST(500300,"订单信息不存在"),

    EMPTY_STOCK(500500,"库存不足"),

    REQUEST_ILLEGAL(50020,"请求非法，请重新尝试"),

    ERROR_CAPTCHE(500300,"验证码错误"),

    BIND_ERROR(500212,"参数校验异常");

    //状态码
    private final  Integer code;
    //状态信息
    private final  String message;
}
