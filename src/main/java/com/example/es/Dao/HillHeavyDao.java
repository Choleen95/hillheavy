package com.example.es.Dao;

import com.example.es.pojo.HillHeavy;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 山沉
 * @公众号 九月的山沉
 * @微信号 Applewith520
 * @个人博客 https://choleen95.github.io/
 * @博客 https://www.cnblogs.com/Choleen/
 * @since 2020/12/28 22:41
 */
public interface HillHeavyDao extends JpaRepository<HillHeavy,Integer> {
    /**
     * 查询
     * @param username 用户名
     * @author 山沉
     * @date 2020/12/28 22:42
     * @return {@link HillHeavy}
     */
    HillHeavy findHillHeavyByUsername(String username);
}
