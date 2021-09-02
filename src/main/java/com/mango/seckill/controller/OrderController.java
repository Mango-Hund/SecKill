package com.mango.seckill.controller;


import com.mango.seckill.pojo.Order;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.IGoodsService;
import com.mango.seckill.service.IOrderService;
import com.mango.seckill.vo.GoodsVo;
import com.mango.seckill.vo.OrderDetailVo;
import com.mango.seckill.vo.RespBean;
import com.mango.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final IOrderService orderService;
    private final IGoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean info(SeckillUser user, @RequestParam("orderId") long orderId) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.LOG_IN_AGAIN);
        }
        Order order = orderService.getById(orderId);
        if(order == null) {
            return RespBean.error(RespBeanEnum.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoods(goods);
        return RespBean.success(orderDetailVo);
    }

}
