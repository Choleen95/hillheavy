package com.example.es.utils;

import org.omg.CORBA.PUBLIC_MEMBER;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 加密、解密
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/5/20 22:53]
 */
public class AesCryptUtils {

    /**
     * 加密工作密钥
     * @param content
     * @param cloneRootKey
     * @param salt
     * @param aesType
     * @author 山沉
     * @date 2021/5/24 21:26
     * @return {@link String}
     */
    public static String encryptAes(String content, byte[] cloneRootKey, byte[] salt, String aesType) throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] cipherAes = encryptCipherAes(content.getBytes(AesSecurityUtils.DEFAULT_CHARSET), cloneRootKey, salt, aesType);
        return "security:"+HexUtils.byte2HexStr(salt,true)+":"+HexUtils.byte2HexStr(cipherAes,true);
    }

    /**
     * 解密工作密钥
     * @param encryptWorkKey 加密得工作密钥
     * @param cloneRootKey 根密钥
     * @param salt 盐值
     * @param aesType 加密算法
     * @author 山沉
     * @date 2021/5/24 22:01
     * @return {@link String}
     */
    public static String decryptAes(String encryptWorkKey, byte[] cloneRootKey, String aesType) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        int index = encryptWorkKey.indexOf("security:");
        if(index == -1){
            throw new IllegalArgumentException("encryptWorkKey is illegal,the front not contains security:");
        }else {
            String subDetail = encryptWorkKey.substring("security:".length());
            String saltStr = subDetail.split(":")[0];
            String cipherStr = subDetail.split(":")[1];
            byte[] salt = HexUtils.str2HexByte(saltStr);
            byte[] cipher = HexUtils.str2HexByte(cipherStr);
            byte[] result = decryptCipherAes(cipher, cloneRootKey, salt, aesType);
            return new String(result,AesSecurityUtils.DEFAULT_CHARSET);
        }
    }

    /**
     * AES加密
     * @param plainText 预加密文本
     * @param workKey 工作密钥
     * @param salt 非加密盐值
     * @param aesType 加密方式
     * @author 山沉
     * @date 2021/5/25 21:59
     * @return {@link String}
     */
    public static String encryptAES(String plainText, String workKey, byte[] salt, String aesType) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] key = HexUtils.str2HexByte(workKey);
        byte[] aesResult = encryptCipherAES(plainText.getBytes(AesSecurityUtils.DEFAULT_CHARSET), key, salt, aesType);
        return "security:"+HexUtils.byte2HexStr(salt,true)+":"+HexUtils.byte2HexStr(aesResult,true);
    }

    public static String decryptAES(String encryptPlainTxt,String workKey,String aesType){
        //去除security
        String regStr = "security:*?([0-9A-Z]z{32})\\:*?([A-Z0-9]{32})";
        Pattern compile = Pattern.compile(regStr);
        Matcher matcher = compile.matcher(encryptPlainTxt);
        //捕获
        while (matcher.find()){
            String group = matcher.group();
            System.out.println(group);
        }
        return null;
    }

    private static byte[] encryptCipherAES(byte[] content,byte[] key,byte[] salt,String aesType) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        Cipher cipher = null;
        byte[] keyByte = Arrays.copyOf(key, 16);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte,"AES");
        if(isGcmCipherByType(aesType)){
            cipher = encryptCipher(keySpec,new GCMParameterSpec(128,Arrays.copyOf(salt,12)),"AES/GCM/NoPadding");
        }else {
            cipher = encryptCipher(keySpec,new IvParameterSpec(Arrays.copyOf(salt,16)),"AES/CBC/PKCS5Padding");
        }
        return cipher.doFinal(content);
    }


    /**
     * 加密工作密钥
     * @param content 原始工作密钥byte数组
     * @param key 根密钥
     * @param salt 盐值
     * @param aesType 加密方式
     * @author 山沉
     * @date 2021/5/24 21:47
     * @return {@link byte[]}
     */
    private static byte[] encryptCipherAes(byte[] content,byte[] key,byte[] salt,String aesType) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = null;
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        if(isGcmCipherByType(aesType)){
            cipher = encryptCipher(keySpec,new GCMParameterSpec(128, Arrays.copyOf(salt,12)),"AES/GCM/NoPadding");
        }else {
            cipher = encryptCipher(keySpec,new IvParameterSpec(Arrays.copyOf(salt,16)),"AES/CBC/PKCS5Padding");
        }
        return cipher.doFinal(content);
    }

    private static byte[] decryptCipherAes(byte[] content,byte[] key,byte[] salt,String aesType) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        if(isGcmCipherByType(aesType)){
            cipher = decryptCipher(keySpec,new GCMParameterSpec(128,salt),"AES/GCM/NoPadding");
        }else {
            cipher = decryptCipher(keySpec,new IvParameterSpec(salt),"AES/CBC/PKCS5Padding");
        }
        return  cipher.doFinal(content);
    }

    /**
     * 指定模式，加密过度
     * @param keySpec 钥匙
     * @param algorithmParameterSpec 算法
     * @param cipherType 密码类型
     * @author 山沉
     * @date 2021/5/24 21:50
     * @return {@link Cipher}
     */
    private static Cipher encryptCipher(SecretKeySpec keySpec, AlgorithmParameterSpec algorithmParameterSpec, String cipherType) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        return generateCipher(Cipher.ENCRYPT_MODE,keySpec,algorithmParameterSpec,cipherType);
    }


    /**
     * 指定模式为2 ，解密过度
     * @param keySpec 钥匙
     * @param algorithmParameterSpec 算法
     * @param cipherType 密码类型
     * @author 山沉
     * @date 2021/5/24 21:51
     * @return {@link Cipher}
     */
    private static Cipher decryptCipher(SecretKeySpec keySpec,AlgorithmParameterSpec algorithmParameterSpec,String cipherType) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        return generateCipher(Cipher.DECRYPT_MODE,keySpec,algorithmParameterSpec,cipherType);
    }

    /**
     * 生成Cipher对象
     * @param cryptMode 模式 1 加密；2 解密
     * @param keySpec 定义key
     * @param algorithmParameterSpec 算法
     * @param cipherType 密码类型
     * @author 山沉
     * @date 2021/5/24 21:48
     * @return {@link Cipher}
     */
    private static Cipher generateCipher(int cryptMode,SecretKeySpec keySpec,AlgorithmParameterSpec algorithmParameterSpec,String cipherType) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher instance = Cipher.getInstance(cipherType);
        instance.init(cryptMode,keySpec,algorithmParameterSpec);
        return instance;
    }


    private static boolean isGcmCipherByType(String aesType){
        return "AES_GCM".equals(aesType);
    }

}
