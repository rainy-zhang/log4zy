# log4zy
对于标记了`@Log4zy`注解的类会在代码出口处注入以下代码（由于注入的代码在编译后才生效，idea会提示属性找不到的error，需要安装log4zy-plugin.jar插件）：
```java
@Autowired
private Logger logger;
```

例如：
```java
// @Log4zy(immediate = true)  同步写入（会影响整体执行效率）
@Log4zy // 异步写入
@RestController
public class LoggerController {
    
    @Autowired
    private LoggerService loggerService;
    
    @GetMapping(value = "/log")
    public String log() {
        // IDEA会提示`Cannot resolve symbol 'logger'`，不过还是可以正常运行。
        logger.info("${name}在#{time}审批了${name}的申请，审批结果：%{result}", "张三", "李四", true);
        // 执行成功后会打印出如下信息：
        // 2022-08-17 11:54:15.186  INFO --- [log4zy-1] org.rainy.example.controller.LoggerController.log(22): [张三]在[2022-08-17 11:54:15]审批了[李四]的申请，审批结果：[true]
        return "hello";
    }

}
```


也可以通过实现`LogWriter`来改变日志输出方式（默认输出方式参考：`DefaultLogWriter`类）。
可以参考如下写法：
```java
// 将日志保存到数据库
public class DBLogWriter implements LogWriter  {
        
    @Override
    public void write(LogDetail logDetail) {
        // 写库
        saveLog(logDetail)
    }

    @Override
    public void exceptionHandler(LogDetail logDetail, Throwable throwable) {
        System.err.println(logDetail.getContent());
    }
    
}
```


IDEA插件源码：https://github.com/rainy-zhang/log4zy-plugin
