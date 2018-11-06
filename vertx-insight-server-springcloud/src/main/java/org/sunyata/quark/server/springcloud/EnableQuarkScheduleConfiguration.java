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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.vertx.insight.QuarkExecutor;


@Configuration
@ComponentScan("org.sunyata.quark.server.springcloud")
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties({QuarkServerProperties.class})
public class EnableQuarkScheduleConfiguration {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(EnableQuarkScheduleConfiguration.class);

    @Autowired
    QuarkServerProperties quarkServerProperties;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    QuarkExecutor quarkExecutor;

    @Scheduled(initialDelayString = "${quark.initialDelay:5000}", fixedDelayString = "${quark.fixedDelay:300000}")
    public void retryBusiness() throws Exception {
        if (quarkServerProperties.getRetryEnable()) {
            String serverId = quarkServerProperties.getServerId();
            quarkExecutor.retryByServerId(serverId);
        }
    }

    @Scheduled(initialDelayString = "${quark.initialDelay:10000}", fixedDelayString = "${quark.fixedDelay:300000}")
    public void reBeginBusiness() throws Exception {
        if (quarkServerProperties.getRetryEnable()) {
            quarkExecutor.reBeginByServerId(quarkServerProperties.getServerId());
        }
    }

}
