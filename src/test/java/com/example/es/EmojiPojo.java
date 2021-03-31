package com.example.es;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/3/31 22:37]
 */
public class EmojiPojo {
    private final String description;
    private final boolean supportsFitzpatrick;
    private final List<String> aliases;
    private final List<String> tags;
    private final String unicode;

    public EmojiPojo(String description, boolean supportsFitzpatrick, List<String> aliases, List<String> tags, byte... unicode) {
        this.description = description;
        this.supportsFitzpatrick = supportsFitzpatrick;
        this.aliases = aliases;
        this.tags = tags;
        try {
            this.unicode = new String(unicode, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        return description;
    }

    public boolean isSupportsFitzpatrick() {
        return supportsFitzpatrick;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getUnicode() {
        return unicode;
    }

}
