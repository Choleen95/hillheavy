package com.example.es.utils;


import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/5/20 22:52]
 */
public class AesSecurityUtils {

    private static SecureRandom secureRandom = null;
    public static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 获取安全随机数
     *
     * @return {@link SecureRandom}
     * @author 山沉
     * @date 2021/5/24 21:17
     */
    public static SecureRandom generateSecureRandom() throws NoSuchAlgorithmException {
        SecureRandom source = SecureRandom.getInstanceStrong();
        //是否是真熵源，/dev/random
        boolean predicationResistant = true;
        //随机数
        int cipherLen = 256;
        byte[] nonce = new byte[cipherLen / 8];
        source.nextBytes(nonce);
        //设置种子长度为385
        int entropyBitsRequired = 385;
        //密码引擎 NIST SP800 90A标准
        BlockCipher cipher = new AESEngine();
        //是否获取随机数时刷新熵源，不是，则不同机器定时刷新
        boolean reSeed = false;
        secureRandom = (new SP800SecureRandomBuilder(source, predicationResistant))
                .setEntropyBitsRequired(entropyBitsRequired)
                .buildCTR(cipher, cipherLen, nonce, reSeed);

        return secureRandom;
    }

    /**
     * 获取安全随机数得字节数组
     *
     * @param byteSize 字节大小
     * @return {@link byte[]}
     * @author 山沉
     * @date 2021/5/24 21:17
     */
    public static byte[] getRandomByte(Integer byteSize) throws NoSuchAlgorithmException {
        if (secureRandom == null) {
            generateSecureRandom();
        }
        byte[] bytes = new byte[byteSize];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    /**
     * 获取盐值数组
     *
     * @param random 非加密密钥安全随机数
     * @return {@link byte[]}
     * @author 山沉
     * @date 2021/5/24 20:32
     */
    public static byte[] generateSaltByte(String random) {
        return random == null ? new byte[0] : HexUtils.str2HexByte(random);
    }

    /**
     * 加密工作密钥
     *
     * @param workKey 原始工作密钥 -- 即非加密 密钥安全随机数
     * @return {@link String}
     * @author 山沉
     * @date 2021/5/24 21:19
     */
    public static String encryptWorkKey(String workKey) throws Exception {
        return validateKeyHexLength(workKey) ? AesCryptUtils.encryptAes(workKey, AesRootKeyInitUtils.getCloneRootKey(), AesSecurityUtils.getRandomByte(16), "AES_CBC") : null;
    }

    /**
     * 解密工作密钥
     *
     * @param encryptWorkKey 加密过得工作密钥
     * @return {@link String}
     * @author 山沉
     * @date 2021/5/25 21:43
     */
    public static String decryptWorkKey(String encryptWorkKey) throws Exception {
        return AesCryptUtils.decryptAes(encryptWorkKey, AesRootKeyInitUtils.getCloneRootKey(), "AES_CBC");
    }

    /**
     * AES加密
     * @param plainText 预加密密钥
     * @author 山沉
     * @date 2021/5/25 22:12
     * @return {@link String}
     */
    public static String encryptPlainTxt(String plainText) throws Exception {
        String aesWorkKey = "security:3BEE50795A23E0264C4A42368AEA2DDF:480B703F72139E96D917B61FA321B13D796D46CE26E93C91BDC7CC0949DC5F71367AC57E39356575741EED0023CF5236";
        String key = decryptWorkKey(aesWorkKey);
        return AesCryptUtils.encryptAES(plainText, key, getRandomByte(16), "AES_CBC");
    }

    /**
     * AES解密
     * @param encryptPlainTxt 预解密的文本
     * @author 山沉
     * @date 2021/5/25 23:13
     * @return {@link String}
     */
    public static String decryptPlainTxt(String encryptPlainTxt) throws Exception {
        String aesWorkKey = "security:3BEE50795A23E0264C4A42368AEA2DDF:480B703F72139E96D917B61FA321B13D796D46CE26E93C91BDC7CC0949DC5F71367AC57E39356575741EED0023CF5236";
        String workKey = decryptWorkKey(aesWorkKey);
        return AesCryptUtils.decryptAES(encryptPlainTxt,workKey,"AES_CBC");
    }


    private static boolean validateKeyHexLength(String key) {
        byte[] bytes = HexUtils.str2HexByte(key);
        return bytes.length >= 16;
    }

}
