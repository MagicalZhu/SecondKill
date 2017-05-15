package org.seckill.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillMapper;
import org.seckill.dao.cache.RedisDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Administrator
 * 对RedisDao进行测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class RedisDaoTest {
    @Autowired
    private RedisDao redisDao;
    private long seckillId=1000;
    @Autowired
    private SeckillMapper seckillMapper;
    @Test
    public void testSeckillWithRedisByGetAndPut() throws Exception {
        Seckill seckill=redisDao.getSeckill(seckillId);
        if(seckill==null){
            seckill=seckillMapper.queryById(seckillId);
            if(seckill!=null){
                String result=redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill=redisDao.getSeckill(seckillId);
                System.out.println(seckill);
            }
        }
        System.out.println(seckill);
    }
}