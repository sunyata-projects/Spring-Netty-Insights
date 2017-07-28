/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.server.springcloud;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sunyata.quark.BusinessManager;
import org.sunyata.quark.DefaultBusinessManager;
import org.sunyata.quark.MultipleThreadBusinessManager;
import org.sunyata.quark.server.springcloud.ioc.SpringServiceLocator;
import org.sunyata.quark.server.springcloud.publish.SpringEventEventPublisher;
import org.sunyata.quark.stereotype.BusinessComponent;
import org.sunyata.quark.thread.ExtendableThreadPoolExecutor;
import org.sunyata.quark.thread.QuarkThreadFactory;
import org.sunyata.quark.thread.TaskQueue;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


@Configuration
@ComponentScan("org.sunyata.quark.server.springcloud")
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties({QuarkServerProperties.class})
public class EnableQuarkServerConfiguration {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(EnableQuarkServerConfiguration.class);
    @Autowired
    private QuarkServerProperties quarkServerProperties;

    @Autowired
    ApplicationContext applicationContext;

    @Bean("quarkRunTaskExecutor")
    ExecutorService quarkRunTaskExecutor() {
        logger.info("quark.maxRunTaskExecutor = {}", quarkServerProperties.getMaxRunTaskExecutor());
        //return Executors.newFixedThreadPool(quarkServerProperties.getMaxRunTaskExecutor());
        ThreadFactory factory = new QuarkThreadFactory();
        return  new ExtendableThreadPoolExecutor(0, quarkServerProperties.getMaxRunTaskExecutor(), 2, TimeUnit.MINUTES,
                new TaskQueue(),
                factory);
    }

    @Bean("quarkMultipleRunTaskExecutor")
    ExecutorService quarkMultipleRunTaskExecutor() {
        //return Executors.newFixedThreadPool(quarkServerProperties.getMaxRunTaskExecutor());
        ThreadFactory factory = new QuarkThreadFactory();
        return  new ExtendableThreadPoolExecutor(0, quarkServerProperties.getMaxRunTaskExecutor(), 2, TimeUnit.MINUTES,
                new TaskQueue(),
                factory);
    }

    @Bean()
    BusinessManager asyncBusinessManager(@Qualifier("quarkMultipleRunTaskExecutor") ExecutorService
                                                 quarkMultipleRunTaskExecutor) throws
            Exception {
        logger.info("创建asyncBusinessManager bean");
        MultipleThreadBusinessManager c = new MultipleThreadBusinessManager();
        c.initialize(quarkMultipleRunTaskExecutor);
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(BusinessComponent.class);
        c.initialize(beansWithAnnotation.values());
        c.setServiceLocator(SpringServiceLocator.class);
        c.setEventPublisher(SpringEventEventPublisher.class);
        return c;
    }

    @Bean("syncBusinessManager")
    @Primary
    BusinessManager syncBusinessManager(@Qualifier("quarkRunTaskExecutor") ExecutorService quarkRunTaskExecutor) throws
            Exception {
        logger.info("创建 syncBusinessManager bean");
        BusinessManager c = new DefaultBusinessManager();
        c.initialize(quarkRunTaskExecutor);
        c.setServiceLocator(SpringServiceLocator.class);
        c.setEventPublisher(SpringEventEventPublisher.class);
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(BusinessComponent.class);
        c.initialize(beansWithAnnotation.values());
        return c;
    }

    //    @Autowired
//    @Qualifier("syncBusinessManager")
//    BusinessManager syncBusinessManager;
//
//    @Autowired
//    @Qualifier("syncBusinessManager")
//    BusinessManager syncBusinessManager;

//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
}
