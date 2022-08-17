package org.rainy.log4zy.config;

import org.rainy.log4zy.*;
import org.rainy.log4zy.stuff.DefaultLogWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wt1734
 * create at 2022/8/16 0016 17:17
 */
@Configuration
@ConfigurationProperties(prefix = "log4zy")
public class LoggerConfig implements CommandLineRunner {

    private String level = String.valueOf(Level.DEBUG);
    private int logQueueSize = 50;
    private int threadCoreSize = 5;
    private int threadMaxSize = 10;
    private int threadKeepAliveSeconds = 60;
    private int threadAwaitTerminationSeconds = 60;
    private BlockingQueue<LogDetail> logQueue;

    @Override
    public void run(String... args) {
        ThreadPoolTaskExecutor executor = executor();
        for (int i = 0; i < threadCoreSize; i++) {
            executor.execute(new LogTask(logWriter(), logQueue()));
        }
    }

    @Bean
    @ConditionalOnMissingBean(value = LogWriter.class)
    public LogWriter logWriter() {
        return new DefaultLogWriter();
    }
    
    @Bean
    @ConditionalOnBean(value = LogWriter.class)
    public Logger logger(@Autowired LogWriter logWriter) {
        Level level = Level.valueOf(this.level);
        return new Logger(level, logWriter, logQueue());
    }

    private BlockingQueue<LogDetail> logQueue() {
        if (this.logQueue == null) {
            this.logQueue = new ArrayBlockingQueue<>(logQueueSize);
        }
        return this.logQueue;
    }

    private ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCoreSize);
        executor.setMaxPoolSize(threadMaxSize);
        executor.setKeepAliveSeconds(threadKeepAliveSeconds);
        executor.setAwaitTerminationSeconds(threadAwaitTerminationSeconds);
        executor.setThreadNamePrefix("log4zy-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getLogQueueSize() {
        return logQueueSize;
    }

    public void setLogQueueSize(int logQueueSize) {
        this.logQueueSize = logQueueSize;
    }

    public int getThreadCoreSize() {
        return threadCoreSize;
    }

    public void setThreadCoreSize(int threadCoreSize) {
        this.threadCoreSize = threadCoreSize;
    }

    public int getThreadMaxSize() {
        return threadMaxSize;
    }

    public void setThreadMaxSize(int threadMaxSize) {
        this.threadMaxSize = threadMaxSize;
    }

    public int getThreadKeepAliveSeconds() {
        return threadKeepAliveSeconds;
    }

    public void setThreadKeepAliveSeconds(int threadKeepAliveSeconds) {
        this.threadKeepAliveSeconds = threadKeepAliveSeconds;
    }

    public int getThreadAwaitTerminationSeconds() {
        return threadAwaitTerminationSeconds;
    }

    public void setThreadAwaitTerminationSeconds(int threadAwaitTerminationSeconds) {
        this.threadAwaitTerminationSeconds = threadAwaitTerminationSeconds;
    }

    public BlockingQueue<LogDetail> getLogQueue() {
        return logQueue;
    }

    public void setLogQueue(BlockingQueue<LogDetail> logQueue) {
        this.logQueue = logQueue;
    }

}
