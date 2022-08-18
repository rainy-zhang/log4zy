package org.rainy.log4zy;

import org.rainy.log4zy.stuff.LogPrincipal;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志表达式分析
 * <br>
 * $-输入参数对象，%-输出参数对象，#-内置参数
 * <br>
 * 如：${username}在#{time}审核${name}的申请，审核结果：%{result}
 * <br>
 */
public class LogAnalyzer {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("00.00");
    private static final String UNDEFINED = "undefined";
    private static final String PATTERN = "[$,#,%]?\\{[^\\}]*(\\.[^\\}]+)*\\}|\\%\\{\\}";
    private static final Map<String, LogFunction<Object[], Object, Object, Throwable, Object>> logFunctions = LogPrincipal.defaultFunctions();
    private static LogDetail _logDetail;

    private final Segment segment;

    private LogAnalyzer(Segment segment) {
        this.segment = segment;
    }

    public static String analyzer(LogDetail logDetail) {
        _logDetail = logDetail;
        Segment segment = analyze(logDetail.getOriginContent());
        LogAnalyzer logAnalyzer = new LogAnalyzer(segment);
        return logAnalyzer.translation();
    }

    /**
     * 提取日志分段信息
     *
     * @param originContent 日志原文
     * @return {@link Segment}
     */
    private static Segment analyze(String originContent) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(originContent);
        List<String> sentences = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        int index = 0;
        int sentenceIndex = 0;
        while (matcher.find()) {
            String parameterStr = matcher.group();
            int start = matcher.start();
            int end = matcher.end();

            String sentence = originContent.substring(sentenceIndex, start);
            sentences.add(sentence);
            sentenceIndex = end;

            parameters.add(extractionParameter(parameterStr, index));

            ParameterType type = ParameterType.get(parameterStr.charAt(0));
            if (type != ParameterType.INNER) {
                index++;
            }
        }

        if (sentenceIndex < originContent.length()) {
            sentences.add(originContent.substring(sentenceIndex));
        }

        return new Segment(sentences.toArray(new String[0]), parameters.toArray(new Parameter[0]));
    }

    /**
     * 提取参数信息
     *
     * @param parameterStr 参数表达式
     * @param index        参数索引
     * @return 返回参数名称，比如入参：${username}，出参：username
     */
    private static Parameter extractionParameter(String parameterStr, int index) {
        char mark = parameterStr.charAt(0);
        ParameterType type = ParameterType.get(mark);
        String name;
        if (type == null) {
            type = ParameterType.IN;
            name = parameterStr.substring(1, parameterStr.length() - 1);
        } else {
            name = parameterStr.substring(2, parameterStr.length() - 1);
        }

        // 如果是内置参数，需要从logFunctions中获取value
        if (type == ParameterType.INNER) {
            LogFunction<Object[], Object, Object, Throwable, Object> logFunction = logFunctions.get(name);
            Object value = UNDEFINED;
            if (logFunction != null) {
                value = logFunction.apply(_logDetail.getArguments(), null, _logDetail.getSession(), _logDetail.getThrowable());
            }
            return new Parameter(index, name, type, value);
        }
        return new Parameter(index, name, type, _logDetail.getArguments()[index]);
    }

    /**
     * 翻译日志原文
     * @return 日志译文
     */
    public String translation() {
        StringBuilder content = new StringBuilder();
        String[] sentences = this.segment.sentence;
        Parameter[] parameters = this.segment.parameters;
        for (int i = 0; i < sentences.length; i++) {
            content.append(sentences[i]);
            if (i >= parameters.length) {
                continue;
            }
            String value = getStringValue(parameters[i].value);
            content.append("【").append(value).append("】");
        }
        return content.toString();
    }

    /**
     * 获取字符串类型的参数
     * @param value 原参数
     * @return stringValue
     */
    private String getStringValue(Object value) {
        String stringValue;
        try {
            Class<?> clazz = value.getClass();
            // 基本类型直接toString()
            if (isPrimitive(clazz)) {
                return value.toString();
            }

            if (value instanceof LocalDateTime) {
                stringValue = DATE_TIME_FORMATTER.format((LocalDateTime) value);
            } else if (value instanceof LocalDate) {
                stringValue = DATE_FORMATTER.format((LocalDate) value);
            } else if (value instanceof Date) {
                stringValue = SIMPLE_DATE_FORMAT_THREAD_LOCAL.get().format((Date) value);
            } else if (value instanceof BigDecimal) {
                stringValue = NUMBER_FORMAT.format(value);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                StringBuilder builder = new StringBuilder("[");
                for (Object o : list) {
                    builder.append(toString(o)).append(",");
                }
                builder.deleteCharAt(builder.length()-1);
                builder.append("]");
                stringValue = builder.toString();
            } else if (value instanceof Map) {
                Map<?,?> map = (Map<?, ?>) value;
                StringBuilder builder = new StringBuilder("{");
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    builder.append(toString(entry.getKey())).append(":").append(toString(entry.getValue())).append(",");
                }
                builder.deleteCharAt(builder.length()-1);
                builder.append("}");
                stringValue = builder.toString();
            } else {
                stringValue = toString(value);
            }
        } finally {
            SIMPLE_DATE_FORMAT_THREAD_LOCAL.remove();
        }
        return stringValue;
    }
    
    private List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (null != clazz) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    
    private String toString(Object entity) {
        Class<?> clazz = entity.getClass();
        if (isPrimitive(clazz)) {
            return entity.toString();
        }
        List<Field> fields = getFields(clazz);
        StringBuilder builder = new StringBuilder("{");
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                Object fieldValue = field.get(entity);
                if (!isPrimitive(fieldType)) {
                    fieldValue = getStringValue(fieldValue);
                }
                builder.append(String.format("%s:%s", fieldName, fieldValue)).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("}");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
    private boolean isPrimitive(Class<?> clazz) {
        return clazz == String.class || clazz == Integer.class || clazz == Long.class
                || clazz == Double.class || clazz == Float.class || clazz == Short.class
                || clazz == Character.class || clazz == Boolean.class;
    }
   

    private static class Segment {
        private final String[] sentence;
        private final Parameter[] parameters;

        public Segment(String[] sentence, Parameter[] parameters) {
            this.sentence = sentence;
            this.parameters = parameters;
        }
    }

    private static class Parameter {
        private final int index;
        private final String name;
        private final ParameterType type;
        private final Object value;

        public Parameter(int index, String name, ParameterType type, Object value) {
            this.index = index;
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }

    /**
     * IN：入参
     * OUT：出参
     * INNER：内置参数 {@link LogPrincipal}
     */
    private enum ParameterType {
        IN,
        OUT,
        INNER;

        public static ParameterType get(char mark) {
            switch (mark) {
                case '$':
                    return IN;
                case '%':
                    return OUT;
                case '#':
                    return INNER;
            }
            return null;
        }

        public static ParameterType getOrDefault(char mark, ParameterType defaultType) {
            ParameterType parameterType = get(mark);
            return parameterType == null ? defaultType : parameterType;
        }

    }

}
