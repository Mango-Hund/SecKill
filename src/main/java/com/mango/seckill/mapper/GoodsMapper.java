package com.mango.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mango.seckill.pojo.Goods;
import com.mango.seckill.pojo.SeckillGoods;
import com.mango.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findGoodsVo();
    GoodsVo getGoodsVoByGoodsId(long goodsId);
    int reduceStock(SeckillGoods seckillGoods);
}
