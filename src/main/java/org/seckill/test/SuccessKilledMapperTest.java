package org.seckill.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017/5/14 0014.
 * @author Administrator
 * 依旧使用Spring和Junit整合
 * */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SuccessKilledMapperTest {
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Test       //向购物车添加数据
    public void insertSuccessKilled() throws Exception {
        long id=1001;
        long userPhone=13772588911L;
        int insertCount=successKilledMapper.insertSuccessKilled(id,userPhone);
        System.out.println("insertCount-->"+insertCount);
    }
    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id=1000;
        long userPhone=13772588911L;
        SuccessKilled successKilled=successKilledMapper.queryByIdWithSeckill(id,userPhone);
        System.out.println(successKilled);       //1000元秒杀iPhone 7 Plus
    }
}