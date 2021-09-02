package com.mango.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.seckill.mapper.OrderMapper;
import com.mango.seckill.mapper.SeckillOrderMapper;
import com.mango.seckill.pojo.Order;
import com.mango.seckill.pojo.SeckillOrder;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.IOrderService;
import com.mango.seckill.vo.GoodsVo;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    private final OrderMapper orderMapper;
    private final SeckillOrderMapper seckillOrderMapper;
    private final RedisTemplate redisTemplate;
    @Override
    public SeckillOrder getSeckillOrderByUserIdGoodsId(Long UserId, long goodsId) {
        return (SeckillOrder) redisTemplate.opsForValue().get("seckillOrder:"+UserId+":"+goodsId);
    }

    @Override
    @Transactional
    public Order createOrder(SeckillUser user, GoodsVo goods) {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setCreateDate(new Date());
        order.setDeliveryAddrId(0L);
        order.setGoodsCount(1);
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setPayDate(new Date());
        orderMapper.insert(order);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrderMapper.insert(seckillOrder);
        redisTemplate.opsForValue().set("seckillOrder:"+user.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }
}
