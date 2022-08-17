package org.rainy.log4zy;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author wt1734
 * create at 2022/8/12 0012 14:19
 */
public class Logger {

    /**
     * 日志最低等级
     */
    private final Level leastLevel;

    /**
     * 日志输出对象
     */
    private final LogWriter logWriter;

    /**
     * 日志队列
     */
    private final BlockingQueue<LogDetail> logQueue;

    public Logger(Level leastLevel, LogWriter logWriter, BlockingQueue<LogDetail> logQueue) {
        this.leastLevel = leastLevel;
        this.logWriter = logWriter;
        this.logQueue = logQueue;
    }

    public void debug(String content, Object... arguments) {
        doLog(content, Level.DEBUG, false, arguments);
    }
    
    public void info(String content, Object... arguments) {
        doLog(content, Level.INFO, false, arguments);
    }
    
    public void warn(String content, Object... arguments) {
        doLog(content, Level.WARN, false, arguments);
    }

    public void error(String content, Object... arguments) {
        doLog(content, Level.ERROR, false, arguments);
    }

    /**
     * 
     * @param originContent 日志原文
     * @param level 日志等级
     * @param immediate 是否直接写入
     * @param arguments 参数列表
     */
    private void doLog(String originContent, Level level, boolean immediate, Object... arguments) {
        if (level.getLevel() < leastLevel.getLevel()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement traceElement = stackTraceElements[3];

        String domain = traceElement.getClassName();
        String kind = traceElement.getMethodName();
        int code = traceElement.getLineNumber();
        
        Throwable throwable = null;
        if (arguments.length > 0) {
            Object argument = arguments[arguments.length - 1];
            if (argument instanceof Throwable) {
                throwable = (Throwable) argument;
            }
        }
        
        LogDetail logDetail = new LogDetail.Builder()
                .originContent(originContent)
                .domain(domain)
                .kind(kind)
                .line(code)
                .logTime(now)
                .level(level)
                .arguments(arguments)
                .throwable(throwable)
                .build();
        // 直接写入
        if (immediate) {
            LogAnalyzer logAnalyzer = LogAnalyzer.analyzer(logDetail);
            String content = logAnalyzer.translation();
            logDetail.setContent(content);
            logWriter.write(logDetail);
            return;
        }
        
        // 异步写入
        if (!logQueue.offer(logDetail)) {
            logWriter.exceptionHandler(logDetail, throwable != null ? throwable : new RuntimeException("log data can not be put task queue!"));
        }
    }
    
    
}