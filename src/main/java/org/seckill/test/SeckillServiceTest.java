package org.seckill.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Administrator
 * Service层的集成测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SeckillServiceTest {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test   //测试获取所有的商品
    public void getSeckillList() throws Exception {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("list={}",list);
        /* 控制台打印日志 : [Seckill{seckillId=1000, name='1000元秒杀iPhone 7 Plus', number=98, startTime=Sun May 14 19:02:23 CST 2017,
                                                   endTime=Tue May 16 00:00:00 CST 2017, createTime=Sun May 14 12:17:19 CST 2017},
                           Seckill{seckillId=1001, name='9000元秒杀Alienware', number=200, startTime=Sun May 14 17:26:42 CST 2017,
                                                   endTime=Tue May 16 00:00:00 CST 2017, createTime=Sun May 14 12:17:19 CST 2017},
                           Seckill{seckillId=1002, name='500元秒杀小米5', number=300, startTime=Sun May 14 17:26:44 CST 2017,
                                                   endTime=Tue May 16 00:00:00 CST 2017, createTime=Sun May 14 12:17:19 CST 2017},
                          Seckill{seckillId=1003, name='200元秒杀红米2', number=400, startTime=Sun May 14 17:26:52 CST 2017,
                                                  endTime=Tue May 16 00:00:00 CST 2017, createTime=Sun May 14 12:17:19 CST 2017}]*/
    }
    @Test       //测试根据id查询商品
    public void getById() throws Exception {
        long seckillId=1000;
        Seckill seckill=seckillService.getById(seckillId);
        logger.info("seckill={}",seckill);
        /* 控制台打印日志 : Seckill{seckillId=1000, name='1000元秒杀iPhone 7 Plus', number=98, startTime=Sun May 14 19:02:23 CST 2017,
                                   endTime=Tue May 16 00:00:00 CST 2017, createTime=Sun May 14 12:17:19 CST 2017} */
    }
    @Test     //判断是否秒杀-->传入商品的Id,获取商品的信根据商品的数量,秒杀时间范围来确定是不是可以进行秒杀
    public void exportSeckillUrl() throws Exception {
        long seckillId=100;
        Exposer exposer= seckillService.exportSeckillUrl(seckillId);
        logger.info("exposer={}",exposer);
        /*控制输出日志 : Exposer{exposed=true, md5='bf204e2683e7452aa7db1a50b5713bae', seckillId=1000, now=0, start=0, end=0}*/
    }
    @Test    //执行秒杀
    public void executeSeckill() throws Exception {
        long seckillId=1000;
        long userPhone=13772588912L;            //已经秒杀过的手机号,就会抛出异常seckill repeated\
        String md5="bf204e2683e7452aa7db1a50b5713bae";
        try {
            SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone,md5);
            logger.info("SeckillExecution={}",seckillExecution);
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }
        catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }
        /*控制台输出日志 :　=SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功',
                                            successKilled=SuccessKilled{seckillId=1000, userPhone=13772588912, state=0,
                                                                                        createTime=Mon May 15 13:15:50 CST 2017,
                                                                                        seckill=Seckill{seckillId=1000,
                                                                                        name='2000元秒杀iphone6', number=98,
                                                                                        startTime=Mon May 15 13:15:50 CST 2017,
                                                            endTime=Wed May 17 12:55:31 CST 2017, createTime=Mon May 15 12:43:41 CST 2017}}}*/
    }
}