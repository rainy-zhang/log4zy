package org.rainy.log4zy;

import java.time.format.DateTimeFormatter;

/**
 * 日志输出对象
 */
public interface LogWriter {
    
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    void write(LogDetail logDetail);
    
    default void exceptionHandler(LogDetail logDetail, Throwable throwable) {
        throwable.printStackTrace();
    }
    
}
