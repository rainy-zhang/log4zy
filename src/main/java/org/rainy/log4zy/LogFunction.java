package org.rainy.log4zy;

/**
 * IN：入参
 * OUT：出参
 * SESSION：会话信息
 * ERROR：异常信息
 * R：返回
 * @author wt1734
 * create at 2022/8/12 0012 14:47
 */
public interface LogFunction<IN, OUT, SESSION, ERROR, R> {
    
    R apply(IN in, OUT out, SESSION session, ERROR error);
    
}
