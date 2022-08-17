package org.rainy.example.service;

import org.rainy.log4zy.Log4zy;
import org.springframework.stereotype.Service;

/**
 * @author wt1734
 * create at 2022/8/17 0017 11:52
 */
@Log4zy
@Service
public class LoggerService {
    
    public String log() {
        return "hello";
    }
    
}
