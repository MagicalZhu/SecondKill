package org.seckill.exception;

/**
 * @author Administrator
 * 秒杀业务相关的所有异常
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
