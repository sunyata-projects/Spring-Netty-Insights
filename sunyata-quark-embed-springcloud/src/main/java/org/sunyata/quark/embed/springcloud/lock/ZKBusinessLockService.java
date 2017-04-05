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

package org.sunyata.quark.embed.springcloud.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.sunyata.quark.embed.springcloud.server.QuarkServerProperties;
import org.sunyata.quark.lock.BusinessLock;
import org.sunyata.quark.lock.BusinessLockService;

/**
 * Created by leo on 17/3/24.
 */
@Component
public class ZKBusinessLockService implements BusinessLockService {
    final Logger LOGGER = LoggerFactory.getLogger(ZKBusinessLockService.class);

    //    private static final String hostString = "172.21.120.223:2181";
    private static final String baseLockPath = "/quark";
//    private static final int timeout = 5 * 1000;
//    private static final ExecutorService testService = Executors.newFixedThreadPool(2);

    @Autowired
    QuarkServerProperties quarkServerProperties;

    private static CuratorFramework curatorClient;

    public synchronized void initCuratorFramework() {
        if (curatorClient == null) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            curatorClient = CuratorFrameworkFactory.builder()
                    .connectString(quarkServerProperties.getZookeeperConnectionString())
                    .connectionTimeoutMs(15000)
                    .sessionTimeoutMs(30000)
                    .retryPolicy(retryPolicy)
                    .build();
            curatorClient.start();
        }
    }

    public synchronized void checkCuratorState() {
        CuratorFrameworkState states = curatorClient.getState();
        if (states == CuratorFrameworkState.STOPPED) {
            curatorClient.start();
        }
    }

    public CuratorFramework getCuratorClient() {
        if (curatorClient == null) {
            initCuratorFramework();
        }
        checkCuratorState();

        return curatorClient;
    }

    @Override
    public BusinessLock getLock(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            LOGGER.error("锁ID不能为空");
            throw new Exception("锁ID不能为空");
        }

        return new ZKLock(new InterProcessMutex(getCuratorClient(), baseLockPath + "/" + path));
    }
}
