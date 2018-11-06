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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.sunyata.quark.server.springcloud.QuarkServerProperties.PREFIX;

/**
 * Created by leo on 17/3/29.
 */
@Component
@ConfigurationProperties(PREFIX)
public class QuarkServerProperties {

    public static final String PREFIX = "quark";

//    @Value("${quark.redis.url}")
//    private String redisUrl;

//    @Value("${quark.redis.password}")
//    private String redisPassword;

    @Value("${quark.zookeeper.connectionString}")
    private String zookeeperConnectionString;


    @Value("${quark.maxRunTaskExecutor:16}")
    private int maxRunTaskExecutor;


    @Value("${quark.log.enable:true}")
    private boolean logEnable;

    public String getServerId() {
        return serverId;
    }

    @Value("${quark.serverId:}")
    private String serverId;

    @Value("${quark.retry.enable:true}")
    private boolean retryEnable;


    @Value("${quark.hystrix.command.threadpool.coresize:100}")
    private int hystrixCommandThreadPoolCoreSize;

    @Value("${quark.hystrix.command.execution.timeout.enable:true}")
    private boolean hystrixCommandExecutionTimeoutEnable;

    @Value("${quark.hystrix.command.execution.timeout.milliseconds:500000}")
    private int hystrixCommandExecutionTimeoutInMilliseconds;


    @Value("${quark.hystrix.command.circuit.breaker.enable:true}")
    private boolean hystrixCommandCircuitBreakerEnable;

    @Value("${quark.hystrix.command.circuit.breaker.volumethreshold:200}")
    private int hystrixCommandCircuitBreakerRequestVolumeThreshold;

    public int getHystrixCommandThreadPoolCoreSize() {
        return hystrixCommandThreadPoolCoreSize;
    }

    public boolean isHystrixCommandExecutionTimeoutEnable() {
        return hystrixCommandExecutionTimeoutEnable;
    }

    public int getHystrixCommandExecutionTimeoutInMilliseconds() {
        return hystrixCommandExecutionTimeoutInMilliseconds;
    }

    public boolean isHystrixCommandCircuitBreakerEnable() {
        return hystrixCommandCircuitBreakerEnable;
    }

    public int getHystrixCommandCircuitBreakerRequestVolumeThreshold() {
        return hystrixCommandCircuitBreakerRequestVolumeThreshold;
    }

    public String getZookeeperConnectionString() {
        return zookeeperConnectionString;
    }

    public QuarkServerProperties setZookeeperConnectionString(String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
        return this;
    }

    public int getMaxRunTaskExecutor() {
        return maxRunTaskExecutor;
    }

    public boolean getRetryEnable() {
        return retryEnable;
    }

    public boolean isRetryEnable() {
        return retryEnable;
    }

    public void setRetryEnable(boolean retryEnable) {
        this.retryEnable = retryEnable;
    }

//    public String getRedisUrl() {
//        return redisUrl;
//    }

//    public String getRedisPassword() {
//        return redisPassword;
//    }

    public boolean isLogEnable() {
        return logEnable;
    }
}
