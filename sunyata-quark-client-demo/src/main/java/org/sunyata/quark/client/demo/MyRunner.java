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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.sunyata.quark.client.IdWorker;
import org.sunyata.quark.client.JsonResponseResult;
import org.sunyata.quark.client.QuarkClient;
import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.QuarkParameterInfo;
import org.sunyata.quark.client.json.Json;

import java.util.List;

@Component
public class MyRunner implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(MyRunner.class);

    @Autowired
    QuarkClient quarkClient;

    @Override
    public void run(String... strings) throws Exception {
        JsonResponseResult<List<BusinessComponentDescriptor>> components = quarkClient.components();
        if (components.getCode() == 0) {
            for (BusinessComponentDescriptor bcd : components.getResponse()) {
                logger.info(bcd.getBisinName());
            }
        }
        IdWorker idWorker = new IdWorker(0, 0);
        QuarkParameterInfo info = new QuarkParameterInfo();

        String serialNo = String.valueOf(idWorker.nextId());
        JsonResponseResult singleBusinessComponent = quarkClient.create(serialNo, "ParallelBusinessComponent", Json
                .encode(info));
        if (singleBusinessComponent.getCode() == 0) {
            logger.info(serialNo);
            JsonResponseResult run = quarkClient.run(serialNo);
            if (run.getCode() == 0) {
                logger.info("成功");
            }
        }
    }
}