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

package org.sunyata.quark.publish;

import java.util.ServiceLoader;

/**
 * Created by leo on 17/3/16.
 */
public interface EventPublisher {
    static EventPublisherFactory serviceLocatorFactory = null;

    static EventPublisherFactory getServiceLocatorFactory() {
        if (serviceLocatorFactory == null) {
            ServiceLoader<EventPublisherFactory> item = ServiceLoader.load(EventPublisherFactory.class);
            for (EventPublisherFactory next : item) {
                if (next instanceof DefaultEventPublisherFactory) {
                    continue;
                }
                return next;
            }
            return new DefaultEventPublisherFactory();
        }
        return serviceLocatorFactory;
    }

    static EventPublisher getPublisher() throws InstantiationException, IllegalAccessException {
        return getServiceLocatorFactory().getPublisher();
    }

    void publish(String serialNo) throws Exception;
}
