package com.mango.seckill.controller;


import com.mango.seckill.pojo.SeckillOrder;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.rabbitmq.MQSender;
import com.mango.seckill.rabbitmq.SeckillMessage;
import com.mango.seckill.service.IGoodsService;
import com.mango.seckill.service.IOrderService;
import com.mango.seckill.service.ISeckillService;
import com.mango.seckill.vo.GoodsVo;
import com.mango.seckill.vo.RespBean;
import com.mango.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    private final IGoodsService goodsService;
    private final IOrderService orderService;
    private final RedisTemplate redisTemplate;
    private final ISeckillService seckillService;
    private final MQSender sender;

    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

    /**
     * 系统初始化,加载商品数量到redis之中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.findGoodsVo();
        if(goodsList == null) {
            return;
        }
        for(GoodsVo goods : goodsList) {
            redisTemplate.opsForValue().set("seckillGoodsStock"+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return 秒杀操作
     */

    @RequestMapping(value = "/{path}/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean list(SeckillUser user, @RequestParam("goodsId")long goodsId,@PathVariable("path") String path) {
            if(user == null) {
                return RespBean.error(RespBeanEnum.LOG_IN_AGAIN);
            }
            //验证path
            boolean check = seckillService.checkPath(user, goodsId, path);
            if(!check){
                return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
            }
            //内存标记
            boolean over = localOverMap.get(goodsId);
            if(over) {
                return RespBean.error(RespBeanEnum.SELL_OUT);
            }
            //预减库存
            long stock = redisTemplate.opsForValue().decrement("seckillGoodsStock"+goodsId);
            if(stock < 0){
                return RespBean.error(RespBeanEnum.SELL_OUT);
            }
            //判断是否已经秒杀到了
            SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
            if(order != null) {
                RespBean.error(RespBeanEnum.REPEATE_ERROR);
            }
            //入队
            SeckillMessage sm = new SeckillMessage();
            sm.setUser(user);
            sm.setGoodsId(goodsId);
            sender.sendSeckillMessage(sm);
            return RespBean.success(0);//排队中
    }

    /**
     * 获取秒杀结果
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public RespBean miaoshaResult(SeckillUser user,@RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.LOG_IN_AGAIN);
        }
        long result  =seckillService.getSeckillResult(user.getId(),goodsId);
        return RespBean.success(result);
    }

    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public RespBean getMiaoshaPath(HttpServletRequest request, SeckillUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    ) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.LOG_IN_AGAIN);
        }
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        String path  = seckillService.createSeckillPath(user, goodsId);
        return RespBean.success(path);
    }

    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public RespBean getMiaoshaVerifyCod(HttpServletResponse response, SeckillUser user,
                                              @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.LOG_IN_AGAIN);
        }
        try {
            BufferedImage image  = seckillService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return RespBean.error(RespBeanEnum.SECKILL_FAIL);
        }
    }
}

