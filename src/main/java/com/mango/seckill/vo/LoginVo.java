package com.mango.seckill.vo;

import com.mango.seckill.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录得到的视图实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {
    @NotNull               //JSR303参数校验
    @IsMobile              //自定义
    private String mobile;

    @Length(max = 32)
    @NotNull
    private String password;
}
