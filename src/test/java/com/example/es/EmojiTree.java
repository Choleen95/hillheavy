package com.example.es;

import javax.xml.soap.Node;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author [山沉]
 * @公众号 [九月的山沉]
 * @个人博客 [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2021/3/31 23:21]
 */
public class EmojiTree {
    private Node root = new Node();

    public EmojiTree(Collection<EmojiPojo> emojis) {
        for (EmojiPojo emoji : emojis) {
            Node tree = root;
            char[] chars = emoji.getUnicode().toCharArray();
            for (char aChar : chars) {
                if(!tree.hasChild(aChar)){
                    tree.addChild(aChar);
                }
                tree = tree.getChild(aChar);
            }
            tree.setEmojiPojo(emoji);
        }
    }


    public EmojiPojo getEmojiPojo(String unicode){
        Node tree = root;
        for (char c : unicode.toCharArray()){
            if(!tree.hasChild(c)){
                return null;
            }
            tree = tree.getChild(c);
        }
        return tree.getEmojiPojo();
    }

    private class Node{
        private Map<Character,Node> children = new HashMap<>();
        private EmojiPojo emojiPojo;

        public Map<Character, Node> getChildren() {
            return children;
        }

        public void setChildren(Map<Character, Node> children) {
            this.children = children;
        }

        public EmojiPojo getEmojiPojo() {
            return emojiPojo;
        }

        public void setEmojiPojo(EmojiPojo emojiPojo) {
            this.emojiPojo = emojiPojo;
        }
        private boolean hasChild(char child){
            return children.containsKey(child);
        }
        private Node getChild(char child){
            return children.get(child);
        }
        public void addChild(char child){
            children.put(child,new Node());
        }
        private boolean isEndOfEmojiPoJo(){
            return emojiPojo != null;
        }
    }

}

