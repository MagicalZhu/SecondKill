package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 *@author Administrator
 * 使用Redis作为缓存
 */
public class RedisDao {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;                        //Jedis池
    private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);      //使用protostuff对Seckill对象进行序列化和反序列化的操作
    public RedisDao(String ip,int port){
        jedisPool=new JedisPool(ip,port);
    }
    public Seckill getSeckill(long seckillId) {         //在判断商品是否可以秒杀时,获取商品直接从Redis中获取
        try {
            Jedis jedis=jedisPool.getResource();        //得到Jedis对象(可以操作Redis)
            try {
                String key = "seckill:" + seckillId;
                //Redis内部并没有实现序列化操作,采用自定义序列化工具-->protostuff(get-->byte[]-->Object对象)
                byte[] bytes = jedis.get(key.getBytes());                   //取出放在Redis中的序列化后的Seckill对象
                if(bytes!=null){                                            //获取到了序列化后的对象,进行反序列化
                    Seckill seckill=schema.newMessage();                    //创建空的Seckill对象
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);       //向空的Seckill对象的放置数据
                    return seckill;
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    public String putSeckill(Seckill seckill) {         //在判断商品是否可以秒杀时,获取商品直接从Redis中获取,如果没有进行put放在Redis中
       try {
            Jedis jedis=jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes=ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                String result=jedis.setex(key.getBytes(),60*60,bytes);          //设置具有超时时间的被序列化的Seckill对象
                return result;
            }finally {
                jedis.close();
            }
       }catch (Exception e){
           logger.error(e.getMessage(),e);
       }
        return null;
    }
}
