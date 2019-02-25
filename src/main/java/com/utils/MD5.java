package com.utils;

import org.springframework.util.DigestUtils;


public class MD5 {
    /**
     * 计算摘要值
     * @param textToDigest
     * @return
     * @throws Exception
     */
    public static String createMD5(String textToDigest) throws Exception {
        //加密后的字符串
        return DigestUtils.md5DigestAsHex(textToDigest.getBytes());
    }
}
