package com.example.es;

import com.example.es.utils.AesRootKeyInitUtils;
import com.example.es.utils.HexUtils;
import com.example.es.utils.AesSecurityUtils;
import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2020/12/30 0:12
 */
public class Demo {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    private static List<EmojiPojo> ALL_EMOJIS = null;

    static {
        InputStream inputStream = Demo.class.getResourceAsStream("/emojis.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            List<EmojiPojo> emoji = getEmoji(reader);
            ALL_EMOJIS = emoji;

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<EmojiPojo> getEmoji(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String read;
        while ((read = reader.readLine()) != null){
            sb.append(read);
        }
        String json = sb.toString();
        try {
            JSONArray emojiJson = new JSONArray(json);
            List<EmojiPojo> emojis = new ArrayList<>(emojiJson.length());
            for (int i = 0; i < emojiJson.length(); i++) {
                JSONObject jsonObject = emojiJson.getJSONObject(i);
                EmojiPojo emojiPojo = buildEmojiFromJSON(jsonObject);
                emojis.add(emojiPojo);
            }
            return emojis;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static EmojiPojo buildEmojiFromJSON(JSONObject json) throws JSONException, UnsupportedEncodingException {
        if(!json.has("emoji")){
            return null;
        }
        byte[] bytes = json.getString("emoji").getBytes("UTF-8");
        String description = null;
        if(json.has("description")){
            description = json.getString("description");
        }
        boolean supportFitzpatrick = false;
        if(json.has("supports_fitzpatrick")){
            supportFitzpatrick = json.getBoolean("supports_fitzpatrick");
        }
        List<String> aliases = jsonArrayToStringList(json.getJSONArray("aliases"));
        List<String> tags = jsonArrayToStringList(json.getJSONArray("tags"));

        return new EmojiPojo(description,supportFitzpatrick,aliases,tags,bytes);
    }

    private static List<String> jsonArrayToStringList(JSONArray array) throws JSONException {
        List<String> strings = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            strings.add(array.getString(i));
        }
        return strings;
    }












    @Test
    public void demo(){
       /* String s = UUID.randomUUID().toString();
        logger.info(s);*/
        List list1 = new ArrayList();
        list1.add(0);
        List list2 = list1;
        System.out.println(list1.get(0) instanceof Integer);
        System.out.println(list2.get(0) instanceof Integer);
    }

    @Test
    public void demo1(){
       String title = "\uD83D\uDE21";
        System.out.println(title.length());
    }


    @Test
    public void demo4(){
        String title = "\uD83D\uDE0A\uD83D\uDE02r的";
        title = EmojiParser.removeAllEmojis(title);
        EmojiPojo emojiPojo = ALL_EMOJIS.get(0);
        EmojiTree emojiTree = new EmojiTree(ALL_EMOJIS);
        EmojiPojo emojiPojo1 = emojiTree.getEmojiPojo(emojiPojo.getUnicode());
        System.out.println(ALL_EMOJIS.size());
        System.out.println(title);
    }


    @Test
    public void testEntropy() throws NoSuchAlgorithmException {
        String str = "123";
        byte[] randomByte = AesSecurityUtils.getRandomByte(16);
        for (byte b : randomByte) {
            System.out.print( b + " ");
        }
        System.out.println();
        String hexStr = HexUtils.byte2HexStr(randomByte, true);
        System.out.println("加密"+hexStr);
        byte[] bytes = HexUtils.str2HexByte(hexStr);
        for (byte aByte : bytes) {
            System.out.print(aByte+" ");
        }
    }

    @Test
    public void testRegExp(){
        String encryptPlainTxt = "security:BA4E82FD45DEC2D8979FB319256A4379:5AFFA1699F6AC5FCA0CE707DEBCC8F77";
        //去除security
        String regStr = "security:*?(\\w{32})\\:*?(\\w{32})";
        Pattern compile = Pattern.compile(regStr);
        Matcher matcher = compile.matcher(encryptPlainTxt);
        //捕获
       while (matcher.find()){
           String group = matcher.group();
           System.out.println("匹配结果:");
           System.out.println(group);
           int beginIndex = matcher.start(1);
           int beginEndIndex = matcher.end(1);
           System.out.println("捕获一："+matcher.group(1) +" 开始索引："+beginIndex +"  结束索引:"+beginEndIndex);
           int endIndex = matcher.start(2);
           int endEndIndex = matcher.end(2);
           System.out.println("捕获二："+matcher.group(2) +" 开始索引："+endIndex +" 结束索引："+endEndIndex);
       }


    }


    @Test
    public void testSubString(){
//        匹配结果:
//        security:BA4E82FD45DEC2D8979FB319256A4379:5AFFA1699F6AC5FCA0CE707DEBCC8F77
//        捕获一：BA4E82FD45DEC2D8979FB319256A4379 开始索引：9  结束索引:41
//        捕获二：5AFFA1699F6AC5FCA0CE707DEBCC8F77 开始索引：42 结束索引：74
        String encryptPlainTxt = "security:BA4E82FD45DEC2D8979FB319256A4379:5AFFA1699F6AC5FCA0CE707DEBCC8F77";
        //的
        String salt = encryptPlainTxt.substring(9, 41);
        System.out.println(salt);
        String cipher = encryptPlainTxt.substring(42, 74);
        System.out.println(cipher);

    }

    @Test
    public void rootKeyTest() throws Exception {
        String first = HexUtils.byte2HexStr(AesSecurityUtils.getRandomByte(16), true);
        String second = HexUtils.byte2HexStr(AesSecurityUtils.getRandomByte(16), true);
        String third = HexUtils.byte2HexStr(AesSecurityUtils.getRandomByte(16), true);
        String salt = HexUtils.byte2HexStr(AesSecurityUtils.getRandomByte(16), true);
        System.out.println("非加密密钥分组：");
        System.out.println(first);
        System.out.println(second);
        System.out.println(third);
        System.out.println(salt);
        //获取rootKey
        String rootKey = AesRootKeyInitUtils.generateRootKey(first, second, third, AesSecurityUtils.generateSaltByte(salt));
        System.out.println("根密钥：");
        System.out.println(rootKey);

        //原始工作密钥
        String workKey = HexUtils.byte2HexStr(AesSecurityUtils.getRandomByte(16), true);
        System.out.println("工作密钥：");
        System.out.println(workKey);
        System.out.println("加密：");
        String encryptWorkKey = AesSecurityUtils.encryptWorkKey(workKey);
        System.out.println(encryptWorkKey);
        System.out.println("解密：");
        String decryptWorkKey = AesSecurityUtils.decryptWorkKey(encryptWorkKey);
        System.out.println(decryptWorkKey);

    }

    @Test
    public void testAesCBC() throws Exception {
        String first = "1C2723F7C77328B082CC382AE40076A5";
        String second = "A6FAD435DEC2906E2298A2E122FDD0AF";
        String third = "74724CA32391B678C0C9C2945B113FCB";
        String salt = "F418038D1ED5D5F9970550B4BC8F938E";
        //解密工作密钥需要根密钥
        String rootKey = AesRootKeyInitUtils.generateRootKey(first, second, third, AesSecurityUtils.generateSaltByte(salt));
        String encryptPlainText = AesSecurityUtils.encryptPlainTxt("123");
        //security:BA4E82FD45DEC2D8979FB319256A4379:5AFFA1699F6AC5FCA0CE707DEBCC8F77
        System.out.println(encryptPlainText);
        //解密
        String plainTxt = AesSecurityUtils.decryptPlainTxt(encryptPlainText);
        System.out.println(plainTxt);

    }




















}
