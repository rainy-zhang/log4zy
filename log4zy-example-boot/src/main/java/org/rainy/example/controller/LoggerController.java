package org.rainy.example.controller;

import org.rainy.example.service.LoggerService;
import org.rainy.log4zy.Log4zy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wt1734
 * create at 2022/8/17 0017 11:11
 */
@Log4zy
@RestController
public class LoggerController {
    
    @Autowired
    private LoggerService loggerService;
    
    @GetMapping(value = "/log")
    public String log() {
        logger.info("${name}在#{time}审批了${name}的申请，审批结果：%{result}", "张三", "李四", true);
        return loggerService.log();
    }

}
