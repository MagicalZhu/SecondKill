package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/5/14 0014.
 * @author Administrator
 */

 //SuccessKilled对应的Dao
@Repository
public interface SuccessKilledMapper {
    //插入购买明细(可以过滤重复)-->订单
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    //根据seckilledId(秒杀商品的id)查询秒杀成功的对象
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone") long userPhone);
}
