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

package org.sunyata.quark.console;

import org.sunyata.quark.AbstractBusinessManager;
import org.sunyata.quark.DefaultBusinessManager;
import org.sunyata.quark.basic.QuarkParameterInfo;
import org.sunyata.quark.console.ioc.SimpleServiceLocator;
import org.sunyata.quark.console.publish.SimpleEventEventPublisher;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.serialno.IdWorker;

/**
 * Created by leo on 17/3/27.
 */
public class Application {

    public static AbstractBusinessManager businessManager = null;

    public static final void main(String[] args) throws Exception {
        businessManager = new DefaultBusinessManager();
        businessManager.setScanPackage("org.sunyata.quark.console.components");
        businessManager.setEventPublisher(SimpleEventEventPublisher.class);
        businessManager.setServiceLocator(SimpleServiceLocator.class);
        businessManager.initialize();


        IdWorker worker = new IdWorker(0, 0);
        for (int i = 0; i < 1; i++) {
            String s3 = String.valueOf(worker.nextId());
            String encode = Json.encode(new QuarkParameterInfo());
            businessManager.create(s3, "ParallelBusinessComponent", encode);
            businessManager.run(s3);
        }
        System.in.read();
    }
}
