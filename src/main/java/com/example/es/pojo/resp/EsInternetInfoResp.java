package com.example.es.pojo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2021/1/19 23:47
 */
@Data
public class EsInternetInfoResp implements Serializable {

    private static final long serialVersionUID = 3717908843883707034L;
    private Long id;
    private String url;
    private String title;
    private String content;
    private String date;
}
