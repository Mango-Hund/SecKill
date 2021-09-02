package com.mango.seckill.controller;

import com.mango.seckill.service.ISeckillUserService;
import com.mango.seckill.vo.LoginVo;
import com.mango.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    ISeckillUserService seckillUserService;

    /**
     * 登录
     * @return 登录页面
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    /**
     *
     * @param loginVo 用户信息
     * @return 若成功则跳转 商品列表页面
     */

    @RequestMapping("/doLogin")
    @ResponseBody //返回json格式的信息
    public RespBean doLogin(LoginVo loginVo,HttpServletRequest request,HttpServletResponse response) {
        log.info(loginVo.toString());
        return seckillUserService.doLogin(loginVo,request,response);
    }
}
