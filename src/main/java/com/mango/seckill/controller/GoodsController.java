package com.mango.seckill.controller;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.IGoodsService;
import com.mango.seckill.vo.GoodsDetailVo;
import com.mango.seckill.vo.GoodsVo;
import com.mango.seckill.vo.RespBean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
@Controller
@RequestMapping("/goods")
@AllArgsConstructor
@Slf4j
public class GoodsController {
    private final IGoodsService goodsService;
    private final RedisTemplate redisTemplate;
    private final ThymeleafViewResolver thymeleafViewResolver;
    private final ApplicationContext applicationContext;

    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toGoodsList(Model model, SeckillUser seckillUser,HttpServletResponse response,HttpServletRequest request){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面，如果不为空，直接返回页面
        String html = (String) valueOperations.get("goodsList");
        if (!ObjectUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", seckillUser);
        List<GoodsVo> goodsVos = goodsService.findGoodsVo();
        model.addAttribute("goodsList",goodsVos);
        //手动渲染
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if(!ObjectUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html,60, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping("/to_detail/{goodsId}")
    @ResponseBody
    public RespBean detail(SeckillUser user, @PathVariable("goodsId") long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setUser(user);
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        return RespBean.success(goodsDetailVo);
    }
}
