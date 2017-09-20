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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.sunyata.quark.MessageDispatchService;
import org.sunyata.quark.QuarkExecutor;

import javax.annotation.PostConstruct;


@Configuration
@ComponentScan("org.sunyata.quark.server.springcloud")
@EnableConfigurationProperties({QuarkServerProperties.class})
public class QuarkServerInitializeConfiguration implements ApplicationContextAware {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(QuarkServerInitializeConfiguration.class);
    private ListableBeanFactory applicationContext;

    @Autowired
    QuarkServerProperties quarkServerProperties;

    @PostConstruct
    public void init() throws Exception {
        messageDispatchService.doDispatch();

    }

    @Autowired
    QuarkExecutor quarkExecutor;

    @Autowired
    MessageDispatchService messageDispatchService;


//
//    @Scheduled(initialDelayString = "${quark.initialDelay:5000}", fixedDelayString = "${quark.fixedDelay:300000}")
//    public void retryBusiness() throws Exception {
//        if (quarkServerProperties.getRetryEnable()) {
//            quarkExecutor.retry();
//        }
//    }
//
//    @Scheduled(initialDelayString = "${quark.initialDelay:10000}", fixedDelayString = "${quark.fixedDelay:300000}")
//    public void reBeginBusiness() throws Exception {
//        if (quarkServerProperties.getRetryEnable()) {
//            quarkExecutor.reBegin();
//        }
//    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
