package org.seckill.exception;

/**
 * @author Administrator
 * 重复秒杀异常(运行期异常)不需要我们手动try catch
 *  Mysql只支持运行期异常的回滚操作
 */
    public class RepeatKillException extends SeckillException {

        public RepeatKillException(String message) {
            super(message);
        }
        public RepeatKillException(String message, Throwable cause) {
            super(message, cause);
        }
}
