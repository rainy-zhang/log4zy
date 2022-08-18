package org.rainy.log4zy;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 日志详细信息
 */
public class LogDetail {

    /**
     * 日志原文
     */
    private String originContent;

    /**
     * 日志译文
     */
    private String content;
    
    private String domain;
    
    private String kind;
    
    private int line;
    
    private LocalDateTime logTime;
    
    private Level level;
    
    private Duration duration;
    
    private Object[] arguments;
    
    private Object session;
    
    private Throwable throwable;
    
    private LogDetail(Builder builder) {
        this.originContent = builder.originContent;
        this.content = builder.content;
        this.domain = builder.domain;
        this.kind = builder.kind;
        this.line = builder.line;
        this.logTime = builder.logTime;
        this.level = builder.level;
        this.duration = builder.duration;
        this.arguments = builder.arguments;
        this.session = builder.session;
        this.throwable = builder.throwable;;
    }

    public static class Builder {
        
        private String originContent;
        private String content;

        private String domain;

        private String kind;

        private int line;

        private LocalDateTime logTime;
        
        private Duration duration;

        private Level level;

        private Object[] arguments;

        private Object session;

        private Throwable throwable;
        
        public LogDetail build() {
            return new LogDetail(this);
        }
        
        public Builder originContent(String originContent) {
            this.originContent = originContent;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder kind(String kind) {
            this.kind = kind;
            return this;
        }

        public Builder line(int line) {
            this.line = line;
            return this;
        }

        public Builder logTime(LocalDateTime logTime) {
            this.logTime = logTime;
            return this;
        }

        public Builder level(Level level) {
            this.level = level;
            return this;
        }

        public Builder arguments(Object[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder session(Object session) {
            this.session = session;
            return this;
        }

        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }
        
        
    }

    public String getOriginContent() {
        return originContent;
    }

    public void setOriginContent(String originContent) {
        this.originContent = originContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Object getSession() {
        return session;
    }

    public void setSession(Object session) {
        this.session = session;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
