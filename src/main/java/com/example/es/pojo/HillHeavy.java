package com.example.es.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @Github https://github.com/Choleen95
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2020/12/28 22:34
 */
@Data
@Entity(name = "t_hill_heavy")
public class HillHeavy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private Boolean handsome;
    private String gender;
    private Integer high;
    private boolean rich;

    @Override
    public String toString() {
        return "HillHeavy{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", handsome=" + handsome +
                ", gender='" + gender + '\'' +
                ", high=" + high +
                ", rich=" + rich +
                '}';
    }
}
