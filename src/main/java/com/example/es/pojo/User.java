package com.example.es.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Choleen
 * @since 2020/12/25 22:56
 **/
@Data
@Entity(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private Integer enabled;
    private Integer locked;
}
