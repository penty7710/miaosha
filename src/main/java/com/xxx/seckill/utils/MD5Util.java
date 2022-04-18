package com.xxx.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

/**
 * Created by 彭天怡 2022/4/13.
 */
public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    /**
     *将用户输入的密码进行加密
     */
    public static String inputPassToFromPass(String inputPass){
        String str = "" + salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    /**
     *将密码指定的盐进行加密
     */
    public static String fromPassToDBPass(String formPass,String salt){
        String str = "" + salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputpass,String salt){
        String frompass = inputPassToFromPass(inputpass);
        String dbpass = fromPassToDBPass(frompass,salt);
        return dbpass;
    }


    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("123456"));
        System.out.println(fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9",salt));
        System.out.println(inputPassToDBPass("123456",salt));

    }
}
