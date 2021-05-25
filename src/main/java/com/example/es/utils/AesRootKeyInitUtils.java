package com.example.es.utils;


import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.provider.symmetric.PBEPBKDF2;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 获取根密钥
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/5/24 20:29]
 */
public class AesRootKeyInitUtils {

    private static byte[] rootKey = null;

    public static String generateRootKey(String first,String second,String third,byte[] salt){
        byte[] firstByte = HexUtils.str2HexByte(first);
        byte[] secondByte = HexUtils.str2HexByte(second);
        byte[] thirdByte = HexUtils.str2HexByte(third);
        int minLen = getMinLength(firstByte.length,secondByte.length,thirdByte.length);
        if(!isKeyAndSaltValue(minLen,salt.length)){
            throw new IllegalArgumentException("Salt Length less than 16");
        }else {
            //构造一个char数组
            char[] combined = new char[minLen];
            for (int i = 0; i < combined.length; i++) {
                combined[i] = (char) (firstByte[i] ^ secondByte[i] ^ thirdByte[i]);
                //加密
                rootKey = encryptPKDFSParameter(combined,salt,1000,16);
                return "security:"+HexUtils.byte2HexStr(rootKey,true);
            }
            return null;
        }
    }

    public static byte[] getCloneRootKey(){
        return rootKey.clone();
    }
    /**
     * aes加密
     * @param content 非加密盐值分组异或组合char数组
     * @param salt 非加密盐值
     * @param iterationCount 迭代次数
     * @param cipherLength 密码得最小长度
     * @author 山沉
     * @date 2021/5/24 20:59
     * @return {@link byte[]}
     */
    private static byte[] encryptPKDFSParameter(char[] content,byte[] salt,int iterationCount,int cipherLength){
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(content),salt,iterationCount);
        KeyParameter keyParameter = (KeyParameter) generator.generateDerivedMacParameters(cipherLength * 8);
        return keyParameter.getKey();
    }

    /**
     * 获取最小长度
     * @param first 非加密密钥分组一长度
     * @param second 非加密密钥分组二长度
     * @param third 非加密密钥分组三 长度
     * @author 山沉
     * @date 2021/5/24 20:35
     * @return {@link int}
     */
    private static int getMinLength(int first,int second,int third){
        int minLen = first;
        if(first > second){
            minLen = second;
        }
        if(second > third){
            minLen = third;
        }
        return minLen;
    }

    private static boolean isKeyAndSaltValue(int minLength,int saltLen){
        boolean isKeyLength = minLength >= 16;
        boolean isSaltLength = saltLen >= 16;
        return isKeyLength & isSaltLength;
    }
}
