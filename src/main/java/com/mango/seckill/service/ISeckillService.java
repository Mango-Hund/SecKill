package com.mango.seckill.service;

import com.mango.seckill.pojo.Order;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.vo.GoodsVo;

import java.awt.image.BufferedImage;

public interface ISeckillService {

    Order seckill(SeckillUser user, GoodsVo goods);
    long getSeckillResult(Long userId, long goodsId);

    boolean checkPath(SeckillUser user, long goodsId, String path);

    boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode);

    String createSeckillPath(SeckillUser user, long goodsId);

    BufferedImage createVerifyCode(SeckillUser user, long goodsId);
}
