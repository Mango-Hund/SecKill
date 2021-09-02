package com.mango.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"登录成功"),
    ERROR(500,"服务器端很异常"),
    //登录模块5002xx
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号格式错误"),
    BIND_ERROR(50022,"参数校验错误"),
    MOBILE_NOT_EXIST(50023,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(50024,"密码更新失败"),
    LOG_IN_AGAIN(50025,"请重新登录"),
    //秒杀模块5005xx
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"该商品每人限购一件"),
    SELL_OUT(500502,"售罄"),
    ORDER_NOT_EXIST(500503,"订单不存在"),
    REQUEST_ILLEGAL(500504,"请求错误"),
    SECKILL_FAIL(500505,"秒杀失败"),
    ;
    private final Integer code;
    private final String message;
}

