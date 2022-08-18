package org.rainy.log4zy;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

public class Logger {

    /**
     * 日志最低等级
     */
    private final Level leastLevel;

    /**
     * 日志输出对象
     */
    private final LogWriter logWriter;
    
    private Boolean immediate = null;

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
        doLog(content, Level.DEBUG, arguments);
    }
    
    public void info(String content, Object... arguments) {
        doLog(content, Level.INFO, arguments);
    }
    
    public void warn(String content, Object... arguments) {
        doLog(content, Level.WARN, arguments);
    }

    public void error(String content, Object... arguments) {
        doLog(content, Level.ERROR, arguments);
    }

    /**
     * 
     * @param originContent 日志原文
     * @param level 日志等级
     * @param arguments 参数列表
     */
    private void doLog(String originContent, Level level, Object... arguments) {
        if (level.getLevel() < leastLevel.getLevel()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement traceElement = stackTraceElements[3];

        String domain = traceElement.getClassName();
        String kind = traceElement.getMethodName();
        int code = traceElement.getLineNumber();
        
        if (immediate == null) {
            try {
                Class<?> clazz = Class.forName(domain);
                Log4zy log4zy = clazz.getAnnotation(Log4zy.class);
                if (log4zy != null) {
                    immediate = log4zy.immediate();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

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
        if (immediate != null && immediate) {
            String content = LogAnalyzer.analyzer(logDetail);
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
