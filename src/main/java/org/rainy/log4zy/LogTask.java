package org.rainy.log4zy;

import java.util.concurrent.BlockingQueue;

/**
 * @author wt1734
 * create at 2022/8/16 0016 17:39
 */
public class LogTask implements Runnable {
    
    private final LogWriter logWriter;
    private final BlockingQueue<LogDetail> logQueue;

    public LogTask(LogWriter logWriter, BlockingQueue<LogDetail> logQueue) {
        this.logWriter = logWriter;
        this.logQueue = logQueue;
    }

    @Override
    public void run() {
        LogDetail logDetail = null;
        try {
            logDetail = this.logQueue.take();
            LogAnalyzer logAnalyzer = LogAnalyzer.analyzer(logDetail);
            logDetail.setContent(logAnalyzer.translation());
            logWriter.write(logDetail);
        } catch (Throwable e) {
            logWriter.exceptionHandler(logDetail, e);
        }

    }
}
