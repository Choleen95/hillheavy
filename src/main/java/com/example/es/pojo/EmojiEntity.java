package com.example.es.pojo;

import java.util.List;

/**
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/3/31 22:37]
 */
public class EmojiEntity {
    private final String description;
    private final boolean supportsFitzpatrick;
    private final List<String> aliases;
    private final List<String> tags;
    private final String unicode;
    private final String htmlDec;
    private final String htmlHex;

    public EmojiEntity(String description, boolean supportsFitzpatrick, List<String> aliases, List<String> tags, String unicode, String htmlDec, String htmlHex) {
        this.description = description;
        this.supportsFitzpatrick = supportsFitzpatrick;
        this.aliases = aliases;
        this.tags = tags;
        this.unicode = unicode;
        this.htmlDec = htmlDec;
        this.htmlHex = htmlHex;
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

    public String getHtmlDec() {
        return htmlDec;
    }

    public String getHtmlHex() {
        return htmlHex;
    }
}
