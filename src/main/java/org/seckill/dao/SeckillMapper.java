package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/14 0014.
 * @author Administrator
 */

//Seckill对应的Dao
@Repository
public interface SeckillMapper {
    //减少库存的方法
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);
    // 根据seckillId(秒杀商品的id)查询商品的
    Seckill queryById(long seckillId);
    //获取所有的商品
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
