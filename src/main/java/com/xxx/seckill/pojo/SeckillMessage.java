package com.xxx.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 彭天怡 2022/4/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private User user;

    private Long goodsId;
}
