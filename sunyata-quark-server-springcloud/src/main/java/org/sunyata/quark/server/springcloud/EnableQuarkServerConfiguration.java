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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sunyata.quark.*;
import org.sunyata.quark.server.springcloud.ioc.SpringServiceLocator;
import org.sunyata.quark.server.springcloud.publish.SpringEventEventPublisher;
import org.sunyata.quark.stereotype.BusinessComponent;

import java.util.Map;


@Configuration
@ComponentScan("org.sunyata.quark.server.springcloud")
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties({QuarkServerProperties.class})
public class EnableQuarkServerConfiguration {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(EnableQuarkServerConfiguration.class);

    @Autowired
    ApplicationContext applicationContext;

    @Bean("syncBusinessManager")
    @Primary
    BusinessManager syncBusinessManager() throws Exception {
        BusinessManager c = new DefaultBusinessManager();
        c.setServiceLocator(SpringServiceLocator.class);
        c.setEventPublisher(SpringEventEventPublisher.class);
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(BusinessComponent.class);
        c.initialize(beansWithAnnotation.values());
        return c;
    }

//    @Bean
//    public RedissonClient redissonClient() {
//        Config config = new Config();
//        String[] urls = quarkServerProperties.getRedisUrl().split(",");
//        logger.info("redis密码为:{}", quarkServerProperties.getRedisPassword());
//        if (urls.length == 1) {
//            SingleServerConfig singleServerConfig = config.useSingleServer().setAddress(quarkServerProperties
//                    .getRedisUrl());
//            if (!StringUtils.isEmpty(quarkServerProperties.getRedisPassword())) {
//                singleServerConfig.setPassword(quarkServerProperties.getRedisPassword());
//            }
//        } else {
//            ClusterServersConfig clusterServersConfig = config.useClusterServers().addNodeAddress(urls[0], urls[1]);
//            if (!StringUtils.isEmpty(quarkServerProperties.getRedisPassword())) {
//                clusterServersConfig.setPassword(quarkServerProperties.getRedisPassword());
//            }
//
//        }
//        return Redisson.create(config);
//    }

    @Bean
    MessageQueueService messageQueueService() {
        return new MessageQueueService();
    }

    @Bean
    QuarkExecutor quarkExecutor(QuarkServerProperties quarkServerProperties){
        return new DefaultQuarkExecutor(quarkServerProperties.getServerId());
    }
    @Bean
    MessageDispatchService messageDispatchService(QuarkExecutor
                                                          quarkExecutor, MessageQueueService
                                                          messageQueueService, QuarkServerProperties
                                                          quarkServerProperties) {
        QuarkCommandConfig config = new QuarkCommandConfig();
        config.setHystrixCommandCircuitBreakerEnable(quarkServerProperties.isHystrixCommandCircuitBreakerEnable())
                .setHystrixCommandCircuitBreakerRequestVolumeThreshold(quarkServerProperties
                        .getHystrixCommandCircuitBreakerRequestVolumeThreshold())
                .setHystrixCommandExecutionTimeoutEnable(quarkServerProperties.isHystrixCommandExecutionTimeoutEnable())
                .setHystrixCommandExecutionTimeoutInMilliseconds(quarkServerProperties
                        .getHystrixCommandExecutionTimeoutInMilliseconds())
                .setHystrixCommandThreadPoolCoreSize(quarkServerProperties.getHystrixCommandThreadPoolCoreSize());
        return new MessageDispatchService(quarkExecutor,messageQueueService, config);
    }
}
