package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by Administrator on 2017/5/14 0014.
 * @author Administrator
 * 业务接口
 */
public interface SeckillService {
    /** 显示秒杀商品的记录 */
    List<Seckill> getSeckillList();
    /** 根据商品的id查询单个商品的秒杀记录 */
    Seckill getById(long seckillId);

    /** 秒杀开启时,输出秒杀接口的地址(防止用户对URL拼接,使用浏览器插件进行秒杀),否则输出系统时间和秒杀时间 */
    Exposer exportSeckillUrl(long seckillId);
    /** 执行秒杀操作*/
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException;

    /** 执行秒杀操作通过存储过程 */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) throws SeckillException;
}
