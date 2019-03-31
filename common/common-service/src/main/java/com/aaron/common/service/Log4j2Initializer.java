package com.aaron.common.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.net.URI;

/**
 * Created by Aaron Sheng on 2017/12/20.
 */
public class Log4j2Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private String CONFIG_FILE = "log4j2.xml";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();

            String loggerFileName = environment.getProperty("config_logger_file");
            if (isFileExisted(loggerFileName)) {
                setConfigLocation(new File(loggerFileName).toURI());
                return;
            }

            String sysLoggerFileName = System.getenv("config_logger_file");
            if (isFileExisted(sysLoggerFileName)) {
                setConfigLocation(new File(sysLoggerFileName).toURI());
                return;
            }

            ClassPathResource configFileClasspathRes = new ClassPathResource(CONFIG_FILE);
            if (configFileClasspathRes.exists()) {
                setConfigLocation(configFileClasspathRes.getURI());
                return;
            }

            configLog4j2(environment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isFileExisted(String fileName) {
        if (fileName != null) {
            File file = new File(fileName);
            return file.exists();
        }
        return false;
    }

    private void setConfigLocation(URI uri) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.setConfigLocation(uri);
    }

    private void configLog4j2(ConfigurableEnvironment environment) {
        String appName = environment.getProperty("spring.application.name");

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("Config");

        LayoutComponentBuilder consoleLayoutBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} %blue{[%12.12t]} %highlight{%5level} %cyan{[TraceId(%X{X-B3-TraceId}),SpanId(%X{X-B3-SpanId}),ParentSpanId(%X{X-B3-ParentSpanId})]} %cyan{%-40.40c{1.}} %msg%n%throwable")
            .addAttribute("charset", "UTF-8");

        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target",
            ConsoleAppender.Target.SYSTEM_OUT)
            .add(consoleLayoutBuilder);
        builder.add(appenderBuilder);

        // 过滤hibernate输出
        builder.add(builder.newLogger("org.hibernate", Level.ERROR)
            .add(builder.newAppenderRef("Stdout"))
            .addAttribute("additivity", false));

        // 有profile时才将日志输出到文件中
        if (environment.getActiveProfiles().length == 0 || appName.equals("bootstrap")) {
            // com.aaron输出debug日志
            builder.add(builder.newLogger("com.aaron", Level.DEBUG)
                .add(builder.newAppenderRef("Stdout"))
                .addAttribute("additivity", false));

            builder.add(builder.newRootLogger(Level.INFO)
                .add(builder.newAppenderRef("Stdout")));
        } else {
            LayoutComponentBuilder rollingLayoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy.MM.dd HH:mm:ss} [%12.12t] %5level [TraceId(%X{X-B3-TraceId}),SpanId(%X{X-B3-SpanId}),ParentSpanId(%X{X-B3-ParentSpanId})] %-40.40c{1.} %msg%n%throwable")
                .addAttribute("charset", "UTF-8");

            ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
                .addComponent(builder.newComponent("TimeBasedTriggeringPolicy").addAttribute("interval", "1").addAttribute("modulate", "true"))
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "512 MB"))
                .addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", "10"));

            appenderBuilder = builder.newAppender("Rolling", "RollingFile")
                .addAttribute("fileName", "/data/" + appName + "/logs/" + appName + ".log")
                .addAttribute("filePattern", "/data/" + appName + "/logs/" + appName + "-%d{MM-dd-yy}.log.gz")
                .add(rollingLayoutBuilder)
                .addComponent(triggeringPolicy);
            builder.add(appenderBuilder);

            builder.add(builder.newRootLogger(Level.INFO)
                .add(builder.newAppenderRef("Stdout"))
                .add(builder.newAppenderRef("Rolling")));
        }

        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.start(builder.build());
    }
}
