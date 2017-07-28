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

    public int getMaxRunTaskExecutor() {
        return maxRunTaskExecutor;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }


    @Value("${quark.maxRunTaskExecutor:16}")
    private int maxRunTaskExecutor;

    @Value("${quark.maximumPoolSize:16}")
    private int maximumPoolSize;

    public boolean isLogEnable() {
        return logEnable;
    }

    @Value("${quark.log.enable:true}")
    private boolean logEnable;


//    @Value("${quark.scanPackages}")
//    private String scanPackages;

    @Value("${quark.retry.enable:true}")
    private boolean retryEnable;

    @Value("${quark.zookeeper.connectionString}")
    private String zookeeperConnectionString;

    public String getZookeeperConnectionString() {
        return zookeeperConnectionString;
    }

    public QuarkServerProperties setZookeeperConnectionString(String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
        return this;
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
}
