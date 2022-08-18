package org.rainy.log4zy.stuff;

import org.rainy.log4zy.LogFunction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class LogPrincipal {

    private static final String TIME = "time"; 
    private static final String DATE = "date";
    public static final Map<String, LogFunction<Object[], Object, Object, Throwable, Object>> functions = new HashMap<>();
    static {
        functions.put(TIME, (in, out, session, error) -> LocalDateTime.now());
        functions.put(DATE, (in, out, session, error) -> LocalDate.now());
    }

    public static Map<String, LogFunction<Object[], Object, Object, Throwable, Object>> defaultFunctions() {
        return functions;
    }
    
    private LogPrincipal() {
        
    }
    
}
