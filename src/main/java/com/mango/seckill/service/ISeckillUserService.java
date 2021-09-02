package com.mango.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.vo.LoginVo;
import com.mango.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author guanghao
 * @since 2021-08-22
 */
public interface ISeckillUserService extends IService<SeckillUser> {
    /**
     *
     * @param loginVo
     * @param response
     * @return 登录
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request,HttpServletResponse response);

    SeckillUser getUserByMobile(String mobile);

    SeckillUser getUserByCookie(String seckillUserCookie,HttpServletRequest request, HttpServletResponse response);

    boolean updatePassword(String token, String mobile, String password);
}
