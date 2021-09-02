package com.mango.seckill.vo;

import com.mango.seckill.pojo.SeckillUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailVo {
    private SeckillUser user;
    private GoodsVo goods;
    private int seckillStatus;
    private int remainSeconds;
}
