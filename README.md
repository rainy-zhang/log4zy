# log4zy
添加依赖后，对于标记了`@Log4zy`注解的类会在代码出口处添加以下代码：
```java
@Autowired
private Logger logger;
```

例如：
```java
@Log4zy
@RestController
public class LoggerController {
    
    @Autowired
    private LoggerService loggerService;
    
    @GetMapping(value = "/log")
    public String log() {
        // IDEA会提示`Cannot resolve symbol 'logger'`找不到变量，不过还是可以正常运行。
        logger.info("${name}在#{time}审批了${name}的申请，审批结果：%{result}", "张三", "李四", true);
        return loggerService.log();
    }

}
```


