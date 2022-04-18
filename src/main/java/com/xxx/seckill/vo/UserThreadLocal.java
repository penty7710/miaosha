package com.xxx.seckill.vo;

import com.xxx.seckill.pojo.User;

/**
 * 用来保存拦截器中获取到的用户数据
 * Created by 彭天怡 2022/4/14.
 */
public class UserThreadLocal {

    private static ThreadLocal<User> userThread = new ThreadLocal<>();


    public static void set(User user){
        userThread.set(user);
    }

    public static User get(){
        return userThread.get();
    }


    //防止内存泄露
    public static void remove(){
        userThread.remove();
    }
}
