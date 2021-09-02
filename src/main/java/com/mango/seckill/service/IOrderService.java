package com.mango.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.seckill.pojo.Order;
import com.mango.seckill.pojo.SeckillOrder;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
public interface IOrderService extends IService<Order> {

    SeckillOrder getSeckillOrderByUserIdGoodsId(Long UserId, long goodsId);

    Order createOrder(SeckillUser user, GoodsVo goods);
}
