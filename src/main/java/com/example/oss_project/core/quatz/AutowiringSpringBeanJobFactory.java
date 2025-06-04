package com.example.oss_project.core.quatz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Component
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);  // 🔧 여기서 의존성 자동 주입
        return job;
    }
}