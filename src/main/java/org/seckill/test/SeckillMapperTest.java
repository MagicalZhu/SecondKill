package org.seckill.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillMapper;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/14 0014.
 * @author Administrator
 * 配置Spring和Junit的整合,加载Junit是启动Spring的IOC容器a
 * 需要告诉Junit,Spring配置文件的位置
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SeckillMapperTest {
    @Autowired
    private SeckillMapper seckillMapper;
    @Test   //测试根据商品的id查询商品信息
    public void queryById() throws Exception {
        long id=1000;
        Seckill seckill=seckillMapper.queryById(id);
        System.out.println(seckill.getName());  //1000元秒杀iPhone 7 Plus
    }
    @Test   //查询所有的商品
    public void queryAll() throws Exception {
        List<Seckill> seckills=seckillMapper.queryAll(0,100);
        for (Seckill s:seckills) {
            System.out.println(s.getName());    //1000元秒杀iPhone 7 Plus 9000元秒杀Alienware 500元秒杀小米5  200元秒杀红米2
        }
    }
    @Test   //商品的数量 减1
    public void reduceNumber() throws Exception {
        Date killTime=new Date();
        int UpdateCount=seckillMapper.reduceNumber(1000,killTime);
        System.out.println("买了-->"+UpdateCount);
    }
}