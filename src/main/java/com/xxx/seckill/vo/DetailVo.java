package com.xxx.seckill.vo;

import com.xxx.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 彭天怡 2022/4/15.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailVo {

    private User user;

    private GoodsVo goodsVo;

    private int seckillStatus;

    private int remainSeconds;
}
