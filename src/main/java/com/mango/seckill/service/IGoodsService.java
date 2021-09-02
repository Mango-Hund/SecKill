package com.mango.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.seckill.pojo.Goods;
import com.mango.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();
    GoodsVo getGoodsVoByGoodsId(long goodsId);
    boolean reduceStock(GoodsVo goods);
}
