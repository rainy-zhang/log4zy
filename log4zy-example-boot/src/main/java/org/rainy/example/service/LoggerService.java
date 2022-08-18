package org.rainy.example.service;

import org.rainy.log4zy.Log4zy;
import org.springframework.stereotype.Service;

@Log4zy
@Service
public class LoggerService {
    
    public String log() {
        return "hello";
    }
    
}
