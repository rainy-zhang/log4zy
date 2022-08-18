package org.rainy.log4zy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log4zy {

    /**
     * 是否直接写入
     * @return true：同步写入（会影响整体响应时间），false：异步写入，进入任务队列等待{@link LogTask}线程消费
     */
    boolean immediate() default false;
    
}
