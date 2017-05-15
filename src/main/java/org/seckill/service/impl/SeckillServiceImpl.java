package org.seckill.service.impl;

import org.seckill.SeckillStatEnum;
import org.seckill.dao.SeckillMapper;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * SeckillService接口的实现类
 */
@Service
public class SeckillServiceImpl implements SeckillService{
    private Logger logger= LoggerFactory.getLogger(this.getClass());        //使用日志打印输出信息
    private String salt="shsdssljdd'l.";         //盐值加密
    //注入Dao层
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private RedisDao redisDao;

    public List<Seckill> getSeckillList() {                              /** 显示所有的商品信息 */
        return seckillMapper.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {                             /** 根据商品的Id查询商品 */
        return seckillMapper.queryById(seckillId);
    }

    /** 判断秒杀是不是可以执行*/
    public Exposer exportSeckillUrl(long seckillId) {               //这个方法是优化点:缓存优化
        Seckill seckill=redisDao.getSeckill(seckillId);             //访问Redis的缓存,取出Seckill对象
        if(seckill==null){                                          //如果Redis中没有这个对象
            seckill=seckillMapper.queryById(seckillId);             //从数据库中获取,并且放在Redis中
            if(seckill==null){                                            /* 如果秒杀的商品不存在或者是卖完了,就不执行秒杀*/
                return  new Exposer(false,seckillId);
            }else{
                redisDao.putSeckill(seckill);
            }
        }
        //获取商品的开始时间,结束时间,系统时间
        Date startTime= seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        Date nowTime=new Date();
        if(nowTime.getTime()<startTime.getTime()                    /*在秒杀开始之前或者之后点击秒杀,就不执行秒杀,并且显示系统的时间*/
                ||nowTime.getTime()>endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);            /* 秒杀的条件都满足的时候,返回可以秒杀! */
    }
    //根据商品的id得到对应的MD5盐值加密后的信息,
    private String getMD5(long seckillId){
        String base=seckillId+"/"+salt;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());        //使用Spring的工具类得到MD5盐值加密后的信息
        return  md5;
    }

    /** 具体的执行秒杀 */
    @Transactional      //使用事务
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5==null||!md5.equals(getMD5(seckillId)))
        {
            throw new SeckillException("seckill data rewrite");     //秒杀数据被重写了
        }
        //执行秒杀逻辑:减库存+增加购买明细
        Date nowTime=new Date();
        try{
            int insertCount=successKilledMapper.insertSuccessKilled(seckillId,userPhone);   //尝试想秒杀成功明细的数据表中插入数据,插入成功返回大于1
            if (insertCount<=0)             //如果返回的值小于等于0,就是重复插入
            {
                throw new RepeatKillException("seckill repeated");      //抛出重复秒杀的异常!
            }else {
                int updateCount=seckillMapper.reduceNumber(seckillId,nowTime);      //此时可以尝试去减去库存中货物数量,更新成功返回1
                if (updateCount<=0)                             //返回小于等于0,则说明数量不足或者是不在秒杀时间内
                {
                    throw new SeckillCloseException("seckill is closed");
                }else {
                    //秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息 commit
                    SuccessKilled successKilled=successKilledMapper.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }
        }catch (SeckillCloseException e1)
        {
            throw e1;
        }catch (RepeatKillException e2)
        {
            throw e2;
        }catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            //所以编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error :"+e.getMessage());
        }

    }
}
