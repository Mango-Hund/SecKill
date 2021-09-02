package com.mango.seckill.service.impl;

import com.mango.seckill.pojo.Order;
import com.mango.seckill.pojo.SeckillOrder;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.IGoodsService;
import com.mango.seckill.service.IOrderService;
import com.mango.seckill.service.ISeckillService;
import com.mango.seckill.util.MD5Util;
import com.mango.seckill.util.UUIDUtil;
import com.mango.seckill.vo.GoodsVo;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
@AllArgsConstructor
public class SeckillServiceImpl implements ISeckillService {
    private final IGoodsService goodsService;
    private final IOrderService orderService;
    private final RedisTemplate redisTemplate;

    @Override
    @Transactional
    public Order seckill(SeckillUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if (success){
            Order order = orderService.createOrder(user, goods);
            return order;
        }else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    @Override
    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(seckillOrder != null) {//秒杀成功
            return seckillOrder.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    @Override
    public boolean checkPath(SeckillUser user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = (String) redisTemplate.opsForValue().get("seckillPath"+user.getId()+goodsId);
        return path.equals(pathOld);
    }

    @Override
    public String createSeckillPath(SeckillUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisTemplate.opsForValue().set("seckillPath"+user.getId()+goodsId, str);
        return str;
    }

    @Override
    public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = (Integer) redisTemplate.opsForValue().get("VerifyCode"+user.getId()+goodsId);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisTemplate.delete("VerifyCode"+user.getId()+goodsId);
        return true;
    }

    @Override
    public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisTemplate.opsForValue().set("VerifyCode"+user.getId()+goodsId, rnd);
        //输出图片
        return image;
    }

    private void setGoodsOver(long goodsId){
        redisTemplate.opsForValue().set("goodsOver"+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return (boolean) redisTemplate.opsForValue().get("goodsOver"+goodsId);
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}

