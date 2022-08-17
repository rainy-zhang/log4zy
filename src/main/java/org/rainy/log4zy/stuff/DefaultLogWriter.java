package org.rainy.log4zy.stuff;

import org.rainy.log4zy.LogDetail;
import org.rainy.log4zy.LogWriter;

/**
 * {@link LogWriter}的默认实现
 *
 * @author wt1734
 * create at 2022/8/16 0016 14:12
 */
public class DefaultLogWriter implements LogWriter {

    @Override
    public void write(LogDetail logDetail) {

        String log = String.format("%s  %s --- [%s] %s.%s(%d) %s", 
                DATE_TIME_FORMATTER.format(logDetail.getLogTime()),
                logDetail.getLevel(),
                Thread.currentThread().getName(),
                logDetail.getDomain(),
                logDetail.getKind(),
                logDetail.getLine(),
                logDetail.getContent()
        );
        System.out.println(log);
    }

    @Override
    public void exceptionHandler(LogDetail logDetail, Throwable throwable) {
        System.err.println(logDetail.getContent());
    }
}
