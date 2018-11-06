
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

package org.sunyata.quark.provider.demo.springcloud;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.sunyata.quark.client.EnableQuarkClient;
import org.sunyata.quark.provider.springcloud.EnableQuarkProvider;

@SpringBootApplication
@EnableQuarkProvider
@EnableHystrix
@EnableQuarkClient
public class Application {

//    @Autowired
//    BusinessManager businessManager;

    public static void main(String[] args) {

//        new TestJob().run();
        new SpringApplicationBuilder(Application.class).web(true).run(args);
//		SpringApplication.run(ServerAApplication.class, args);
    }

    //
//    @Bean
//    public AlwaysSampler defaultSampler() {
//        return new AlwaysSampler();
//    }
//    @PostConstruct
//    public void initCoordination() {
//
//    }

//    @Bean
//    BusinessManager businessManager() throws Exception {
//        BusinessManager c = new MultipleThreadBusinessManager();
//        c.setScanPackage("org.sunyata.quark.embed.demo.springcloud.components");
//        c.setServiceLocator(SpringServiceLocator.class);
//        c.setEventPublisher(SpringEventEventPublisher.class);
//        c.initialize();
////        c.register(ParallelBusinessComponent.class);
////        c.register(SingleBusinessComponent.class);
////        c.register(TwoBusinessComponent.class);
//        return c;
//    }
}
