package com.mango.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.seckill.exception.GlobalException;
import com.mango.seckill.mapper.SeckillUserMapper;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.ISeckillUserService;
import com.mango.seckill.util.CookieUtil;
import com.mango.seckill.util.MD5Util;
import com.mango.seckill.util.UUIDUtil;
import com.mango.seckill.vo.LoginVo;
import com.mango.seckill.vo.RespBean;
import com.mango.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
@AllArgsConstructor
@Service
public class SeckillUserServiceImpl extends ServiceImpl<SeckillUserMapper, SeckillUser> implements ISeckillUserService {

    //构造注入
    private final SeckillUserMapper seckillUserMapper;
    private final RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String password = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        //根据手机号获取用户
        SeckillUser seckillUser = getUserByMobile(mobile);
        if (seckillUser==null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password,seckillUser.getSalt()).equals(seckillUser.getPassword())){
            throw new GlobalException(RespBeanEnum.BIND_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        //将cookie加入第三方redis缓存之中
        redisTemplate.opsForValue().set("seckillUser:"+token,seckillUser);
        CookieUtil.setCookie(request,response,"token",token);
        return RespBean.success(token);
    }

    @Override
    public boolean updatePassword(String token, String mobile, String password) {
        //获取用户
        SeckillUser seckillUser = getUserByMobile(mobile);
        if (seckillUser==null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        //更新数据库
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(Long.parseLong(mobile));
        toBeUpdate.setPassword(MD5Util.inputPassToDbPass(password,seckillUser.getSalt()));
        //处理缓存
        redisTemplate.delete("mobile"+seckillUser);
        seckillUser.setPassword(toBeUpdate.getPassword());
        redisTemplate.opsForValue().set("seckillUser:"+token,seckillUser);
        return true;
    }

    @Override
    public SeckillUser getUserByMobile(String mobile) {
        //取缓存
        SeckillUser seckillUser = (SeckillUser) redisTemplate.opsForValue().get("mobile");
        if (seckillUser!=null){
            return seckillUser;
        }
        seckillUser = seckillUserMapper.selectById(mobile);
        if (seckillUser!=null){
            redisTemplate.opsForValue().set("mobile",seckillUser);
        }
        return seckillUser;
    }

    @Override
    public SeckillUser getUserByCookie(String token, HttpServletRequest request, HttpServletResponse response) {
        if (token.length()==0) return null;
        SeckillUser seckillUser = (SeckillUser) redisTemplate.opsForValue().get("seckillUser:"+token);
        if (seckillUser!=null){
            CookieUtil.setCookie(request,response,"token",token);
        }
        return seckillUser;
    }
}
