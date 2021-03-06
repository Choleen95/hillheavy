package com.example.es.vo;

import lombok.Data;

import javax.annotation.security.DenyAll;

/**
 * @author Choleen
 * @since 2020/12/26 0:40
 **/
@Data
public class ResponseBean {
    private Integer code;
    private String message;
    private Object data;

    public static ResponseBean ok(){
       ResponseBean resp = new ResponseBean();
       resp.setCode(200);
       resp.setMessage("your operation success");
       return resp;
    }
    public static ResponseBean ok(String message){
        ResponseBean resp = new ResponseBean();
        resp.setCode(200);
        resp.setMessage(message);
        return resp;
    }

    public static ResponseBean error(){
        ResponseBean resp = new ResponseBean();
        resp.setCode(200);
        resp.setMessage("your operation failure");
        return resp;
    }

    public static ResponseBean error(String message){
        ResponseBean resp = new ResponseBean();
        resp.setCode(500);
        resp.setMessage(message);
        return resp;
    }

    public static ResponseBean sendByCode(String message,Integer code){
        ResponseBean resp = new ResponseBean();
        resp.setCode(code);
        resp.setMessage(message);
        return resp;
    }

    public static ResponseBean sendByData(Integer code,String message,Object data){
        ResponseBean resp = new ResponseBean();
        resp.setCode(code);
        resp.setMessage(message);
        resp.setData(data);
        return resp;
    }
}
