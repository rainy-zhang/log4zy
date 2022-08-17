package org.rainy.log4zy;

import java.time.format.DateTimeFormatter;

/**
 * 日志输出对象
 * @author wt1734
 * create at 2022/8/12 0012 14:46
 */
public interface LogWriter {
    
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    void write(LogDetail logDetail);
    
    void exceptionHandler(LogDetail logDetail, Throwable throwable);
    
}
