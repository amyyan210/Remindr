package com.sweetjandy.remindr;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
public class SpringbootRemindrApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(SpringbootRemindrApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(SpringbootRemindrApplication.class);
    }
}