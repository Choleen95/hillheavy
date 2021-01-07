package com.example.es.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/8 0:30
 */
@Data
@Entity(name = "t_internet_info")
public class InternetInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private String title;
    private String content;
    private Date date;
}
