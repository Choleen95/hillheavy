package com.example.es;

import com.example.es.Dao.HillHeavyDao;
import com.example.es.pojo.HillHeavy;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class EsSearchApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(EsSearchApplicationTests.class);

    @Resource
    private HillHeavyDao hillHeavyDao;

    @Test
    void contextLoads() {
        HillHeavy hillHeavy = new HillHeavy();
        hillHeavy.setUsername("山沉");
        hillHeavy.setHandsome(true);
        hillHeavy.setHigh(180);
        hillHeavy.setGender("男");
        hillHeavy.setRich(true);
        hillHeavyDao.save(hillHeavy);
        logger.info("实体----->{}",hillHeavy);
    }

}
