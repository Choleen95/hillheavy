package com.example.es;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        reader.close();
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
        System.out.println(ALL_EMOJIS.size());
        System.out.println(title);
    }




















}
