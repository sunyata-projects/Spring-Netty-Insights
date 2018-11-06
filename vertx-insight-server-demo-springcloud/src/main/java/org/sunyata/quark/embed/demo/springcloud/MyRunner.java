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
//package org.sunyata.quark.embed.demo.springcloud;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.sunyata.quark.BusinessManager;
//import org.sunyata.quark.json.Json;
//import org.sunyata.quark.serialno.IdWorker;
//
//import java.util.HashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by leo on 17/3/15.
// */
//@Component
//public class MyRunner implements CommandLineRunner {
//    Logger logger = LoggerFactory.getLogger(MyRunner.class);
//    @Autowired
//    @Qualifier("asyncBusinessManager")
//    BusinessManager asyncBusinessManager;
//
//    @Override
//    public void run(String... strings) throws Exception {
//        Thread.sleep(10000);
//        ExecutorService testService = Executors.newFixedThreadPool(1);
//
//        IdWorker worker = new IdWorker(0, 0);
//        String s = String.valueOf(worker.nextId());
//        asyncBusinessManager.create(s, "SingleBusinessComponent", "lcl", "20170987", Json.encode(new HashMap<>()));
//        asyncBusinessManager.run(s);
//
//
////        String s2 = String.valueOf(worker.nextId());
////        businessManager.create(s2, "TwoBusinessComponent", "this is parameterString" + Thread.currentThread()
////                .getName());
//
//
////        for (int i = 0; i < 1; i++) {
////            String s3 = String.valueOf(worker.nextId());
////            String encode = Json.encode(new QuarkParameterInfo());
////            businessManager.create(s3, "ParallelBusinessComponent", encode);
////            businessManager.run(s3);
////        }
////
//
//        //businessManager.run("845222995512262657");
//
//    }
//
////    @Scheduled(fixedRate = 5000)
////    public void retryBusiness() throws Exception {
////        businessManager.retry();
//////        logger.info("The time is now {}", dateFormat.format(new Date()));
////    }
//}
