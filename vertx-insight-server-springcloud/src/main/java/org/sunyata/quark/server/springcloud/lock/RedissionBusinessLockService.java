///*
// *
// *
// *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// *  * use this file except in compliance with the License. You may obtain a copy
// *  * of the License at
// *  *
// *  *   http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// *  * License for the specific language governing permissions and limitations
// *  * under the License.
// *  *
// *
// */
//
//package org.sunyata.quark.server.springcloud.lock;
//
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.sunyata.quark.lock.BusinessLock;
//import org.sunyata.quark.lock.BusinessLockService;
//
///**
// * Created by leo on 17/3/24.
// */
//@Component
//public class RedissionBusinessLockService implements BusinessLockService {
//    final Logger LOGGER = LoggerFactory.getLogger(RedissionBusinessLockService.class);
//
//    //    private static final String hostString = "172.21.120.223:2181";
//    private static final String baseLockPath = "/quark";
//    @Autowired
//    RedissonClient redissonClient;
//
//    @Override
//    public BusinessLock getLock(String path) throws Exception {
//        if (StringUtils.isEmpty(path)) {
//            LOGGER.error("锁ID不能为空");
//            throw new IllegalArgumentException("锁ID不能为空");
//        }
//
//        RLock lock = redissonClient.getLock(path);
//        return new RedisLock(lock);
//    }
//}
