package org.rainy.log4zy;


/**
 * 日志等级
 */
public enum Level {
    
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3);
    
    private final int level;

    Level(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
