//package org.sunyata.quark.server.springcloud;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.config.environment.Environment;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.Trigger;
//import org.springframework.scheduling.TriggerContext;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
///**
// * Created by leo on 17/6/26.
// */
//@Configuration
//@EnableScheduling
//public class RetrySchedulingConfigurer implements SchedulingConfigurer {
//
//    @Autowired
//    Environment env;
//
//    @Bean
//    public MyBean myBean() {
//        return new MyBean();
//    }
//
//    @Bean(destroyMethod = "shutdown")
//    public Executor taskExecutor() {
//        return Executors.newScheduledThreadPool(100);
//    }
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.setScheduler(taskExecutor());
//        taskRegistrar.addTriggerTask(
//                new Runnable() {
//                    @Override public void run() {
//                        myBean().getSchedule();
//                    }
//                },
//                new Trigger() {
//                    @Override public Date nextExecutionTime(TriggerContext triggerContext) {
//                        Calendar nextExecutionTime =  new GregorianCalendar();
//                        Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
//                        nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
//                        nextExecutionTime.add(Calendar.MILLISECOND, env.getProperty("myRate", Integer.class)); //you can get the value from wherever you want
//                        return nextExecutionTime.getTime();
//                    }
//                }
//        );
//    }
