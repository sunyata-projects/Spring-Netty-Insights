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

package org.sunyata.quark.embed.springcloud.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.sunyata.quark.BusinessManager;
import org.sunyata.quark.MultipleThreadBusinessManager;
import org.sunyata.quark.embed.springcloud.ioc.SpringServiceLocator;
import org.sunyata.quark.embed.springcloud.publish.SpringEventEventPublisher;


@Configuration
@ComponentScan("org.sunyata.quark.embed.springcloud")
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties({QuarkServerProperties.class})
public class EnableQuarkServerConfiguration {

    @Autowired
    private QuarkServerProperties quarkServerProperties;

    @Bean
    BusinessManager businessManager() throws Exception {
        BusinessManager c = new MultipleThreadBusinessManager();
        c.setScanPackage(quarkServerProperties.getScanPackages());
        c.setServiceLocator(SpringServiceLocator.class);
        c.setEventPublisher(SpringEventEventPublisher.class);
        c.initialize();
        return c;
    }
}
