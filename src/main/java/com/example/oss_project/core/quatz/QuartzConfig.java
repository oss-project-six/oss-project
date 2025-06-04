package com.example.oss_project.core.quatz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(AutowiringSpringBeanJobFactory jobFactory) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);  // 💡 우리가 만든 JobFactory 등록
        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        return factory.getScheduler();
    }
}