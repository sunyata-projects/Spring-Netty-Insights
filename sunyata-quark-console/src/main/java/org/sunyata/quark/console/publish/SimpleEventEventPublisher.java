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

package org.sunyata.quark.console.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.console.Application;
import org.sunyata.quark.publish.EventPublisher;

/**
 * Created by leo on 17/3/16.
 */
public class SimpleEventEventPublisher implements EventPublisher {
    Logger logger = LoggerFactory.getLogger(SimpleEventEventPublisher.class);
    @Override
    public void publish(String no, String serialNo) throws Exception {
//        SpringContextUtil.getApplicationContext().publishEvent(new BusinessComponentEvent(serialNo).setSerialNo
//                (serialNo));
        logger.info("SimpleEventEventPublisher current Thread:{}",Thread.currentThread().getName());
        Application.businessManager.run(serialNo);
    }
}
