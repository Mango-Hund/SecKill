package com.mango.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.seckill.mapper.GoodsMapper;
import com.mango.seckill.pojo.Goods;
import com.mango.seckill.pojo.SeckillGoods;
import com.mango.seckill.service.IGoodsService;
import com.mango.seckill.vo.GoodsVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    private final GoodsMapper goodsMapper;
    /**
     * 获取商品列表信息
     * @return 商品（对象）列表
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
        int res = goodsMapper.reduceStock(seckillGoods);
        return res>0;
    }
}
