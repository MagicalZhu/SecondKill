-- 创建数据库
CREATE database  seckill;
-- 创建秒杀库存表
create TABLE seckill(
seckill_id bigint NOT NULL Auto_Increment COMMENT '商品库存id',
name VARCHAR(120) NOT NULL COMMENT '商品名称',
number INT NOT NULL COMMENT '库存数量',
start_time TIMESTAMP NOT NULL  COMMENT '秒杀开启时间',
end_time TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
create_time TIMESTAMP NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)Engine InnoDB Auto_Increment=1000 DEFAULT  CHARSET=utf8;

-- 初始化数据
INSERT INTO
  seckill (name,number,start_time,end_time)
VALUES
  ('1000元秒杀iPhone 7 Plus',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
  ('9000元秒杀Alienware',200,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
  ('500元秒杀小米5',300,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
  ('200元秒杀红米2',400,'2015-11-01 00:00:00','2015-11-02 00:00:00');

-- 秒杀成功的明细表(用户登录认证相关的信息)
Create TABLE success_killed (
seckill_id BIGINT NOT NULL  COMMENT '秒杀商品的id',
user_phone BIGINT NOT NULL COMMENT '用户的手机号',
state TINYINT  NOT NULL  DEFAULT -1 COMMENT '状态表示,-1表示无效,0表示成功,1表示已付款,2已发货',
create_time TIMESTAMP NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id,user_phone), /*联合主键*/
key idx_create_time(create_time)
)Engine InnoDB DEFAULT  CHARSET=utf8 COMMENT ' 秒杀成功的明细表';







