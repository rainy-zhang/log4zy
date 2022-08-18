package org.rainy.log4zy.stuff;

import org.rainy.log4zy.LogDetail;
import org.rainy.log4zy.LogWriter;

/**
 * {@link LogWriter}的默认实现
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

}
