package com.example.es.utils;


import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/5/20 22:52]
 */
public class HexUtils {

    private static byte[] UPPER_LETTER = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private static byte[] LOWER_LETTER = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    /**
     * 获取非加密的16进制的字符串 （32）
     * @param data 随机数
     * @param isUpper 是否是大写
     * @author 山沉
     * @date 2021/5/23 15:52
     * @return {@link String}
     */
    public static String byte2HexStr(byte[] data,boolean isUpper){
        return isUpper ? hexEncodeStr(data,UPPER_LETTER) : hexEncodeStr(data,LOWER_LETTER);
    }

    private static String hexEncodeStr(byte[] data,byte[] toDigits){
        int length = data.length;
        if((length & 1) != 0){
            throw new IllegalArgumentException("Character is illegal length");
        }else {
            //长度扩大至32
            byte[] out = new byte[length << 1];
            int var5 = 0;
            for (int i = 0; i < data.length; i++) {
                //高四位 1111 0000 --》 240
                out[var5++] = toDigits[(240 & data[i]) >>> 4];
                //低四为 0000 1111 --》 15
                out[var5++] = toDigits[(15 & data[i])];
            }
            return new String(out,AesSecurityUtils.DEFAULT_CHARSET);
        }
    }
    /**
     * 对16进制数据转换为16 的字节数组
     * @param plainText  非加密16进制字符串
     * @author 山沉
     * @date 2021/5/23 16:06
     * @return {@link byte[]}
     */
    public static byte[] str2HexByte(String plainText){
        return StringUtils.isEmpty(plainText) ? new byte[0] : hexStr2Byte(plainText.toCharArray());
    }

    private static byte[] hexStr2Byte(char[] data){
        int length = data.length;
        //缩小长度
        byte[] out = new byte[length >> 1];
        int var5 = 0;
        for (int i = 0; i < data.length; i++) {
            int f = toDigit(data[i],i)  << 4;
            ++i;
            f |= toDigit(data[i],i) & 15;
            out[var5++] = (byte)(f & 255);
        }
        return out;
    }



    private static int toDigit(char data,int index){
        int digit = Character.digit(data, 16);
        if(digit == -1){
            throw new IllegalArgumentException("Character number is illegal "+data+" at index "+index);
        }
        return digit;
    }
}
