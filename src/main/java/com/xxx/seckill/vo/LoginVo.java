package com.xxx.seckill.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Created by 彭天怡 2022/4/13.
 */
@Data
public class LoginVo {

    @NotNull
    private String mobile;
    @NotNull
    @Length(max = 32)
    private String password;
}
