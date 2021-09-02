package com.mango.seckill.rabbitmq;

import com.mango.seckill.pojo.SeckillUser;
import lombok.Data;

@Data
public class SeckillMessage {
    private SeckillUser user;
    private long goodsId;
}
