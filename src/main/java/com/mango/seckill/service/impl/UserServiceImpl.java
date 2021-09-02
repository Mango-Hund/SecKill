package com.mango.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.seckill.exception.GlobalException;
import com.mango.seckill.mapper.SeckillUserMapper;
import com.mango.seckill.mapper.UserMapper;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.pojo.User;
import com.mango.seckill.service.IUserService;
import com.mango.seckill.util.MD5Util;
import com.mango.seckill.vo.LoginVo;
import com.mango.seckill.vo.RespBean;
import com.mango.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gunghao
 * @since 2021-08-22
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
