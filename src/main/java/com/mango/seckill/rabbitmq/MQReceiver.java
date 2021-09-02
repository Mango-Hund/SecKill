package com.mango.seckill.rabbitmq;

import com.mango.seckill.pojo.SeckillOrder;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.IGoodsService;
import com.mango.seckill.service.IOrderService;
import com.mango.seckill.service.ISeckillService;
import com.mango.seckill.util.JsonUtil;
import com.mango.seckill.vo.GoodsVo;
import com.mango.seckill.vo.RespBean;
import com.mango.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MQReceiver {

    private final IGoodsService goodsService;
    private final IOrderService orderService;
    private final ISeckillService seckillService;

    @RabbitListener(queues=MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        log.info("receive message:"+message);
        SeckillMessage mm  = JsonUtil.jsonStr2Object(message,SeckillMessage.class);
        SeckillUser user = mm.getUser();
        long goodsId = mm.getGoodsId();
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goods);
    }
}
