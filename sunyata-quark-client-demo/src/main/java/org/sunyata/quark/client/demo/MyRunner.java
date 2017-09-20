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

package org.sunyata.quark.client.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.sunyata.quark.client.IdWorker;
import org.sunyata.quark.client.JsonResponseResult;
import org.sunyata.quark.client.QuarkClient;
import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.QuarkParameterInfo;
import org.sunyata.quark.client.json.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class MyRunner implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(MyRunner.class);

    @Autowired
    @Qualifier("ccop-share-quark-client")
    QuarkClient ccopShareQuarkClient;

    @Autowired
    @Qualifier("edy-quark-client")
    QuarkClient edyQuarkClient;

    @Override
    public void run(String... strings) throws Exception {
        JsonResponseResult<List<BusinessComponentDescriptor>> components = edyQuarkClient.components();
        if (components.getCode() == 0) {
            for (BusinessComponentDescriptor bcd : components.getResponse()) {
                logger.info(bcd.getBisinFriendlyName());
            }
        }
        IdWorker idWorker = new IdWorker(0, 0);
        QuarkParameterInfo info = new QuarkParameterInfo();

        String serialNo = String.valueOf(idWorker.nextId());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("key1", "value1");
        parameters.put("key2", "value2");
        parameters.put("key3", "value3");
        parameters.put("key4", "value4");

        generateSerialNo();
        for (int i = 0; i < ids.size(); i++) {

            //edyQuarkClient.createAsync(serialNo, "MatchBusinessComponent", Json.encode(parameters), true);
            //edyQuarkClient.createAsync(serialNo, "SingleBusinessComponent", Json.encode(parameters), true);
            serialNo = ids.get(i);

            edyQuarkClient.createSyncIfNecessary(serialNo, "MatchBusinessComponent",null,null, Json.encode
                    (parameters), true);

        }
        System.out.println(ids);
//        for (int i = 0; i < 10; i++) {
//            serialNo = String.valueOf(idWorker.nextId());
//            edyQuarkClient.createAsync(serialNo, "RetryBusinessComponent", Json.encode(parameters), true);
//            serialNo = String.valueOf(idWorker.nextId());
//            edyQuarkClient.createAsync(serialNo, "TestBusinessComponent", Json.encode(parameters), true);
//        }
        //edyQuarkClient.run(serialNo);
        //edyQuarkClient.runByManual(serialNo, 1, Json.encode(parameters));
        //System.out.println(parallelBusinessComponent.getCode());
//        if (singleBusinessComponent.getCode() == 0) {
//            logger.info(serialNo);
//            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
//            stringObjectHashMap.put("field1", "value1");
//            stringObjectHashMap.put("field34", "value5");
//            JsonResponseResult run = ccopShareQuarkClient.runByManual(serialNo, 1, Json.encode(stringObjectHashMap));
//            if (run.getCode() == 0) {
//                logger.info("成功");
//            }
//        }
    }

    static ArrayList<String> ids = new ArrayList<>();
    static IdWorker idWorker = new IdWorker(0, 1);

    void generateSerialNo() {
        while (ids.size() < 1000) {
            String serialNo = String.valueOf(idWorker.nextId());
            if (ids.contains(serialNo)) {
                continue;
            }
            ids.add(serialNo);
        }
    }
}