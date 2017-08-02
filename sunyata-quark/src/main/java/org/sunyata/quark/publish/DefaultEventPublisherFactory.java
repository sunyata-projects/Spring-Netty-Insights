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

/**
 * Created by leo on 17/3/16.
 */
public class DefaultEventPublisherFactory implements EventPublisherFactory {
    @Override
    public EventPublisher getPublisher() throws IllegalAccessException, InstantiationException {
        if (serviceLocatorClass == null) {
            return new DefaultEventEventPublisher();
        } else {
            return serviceLocatorClass.newInstance();
        }
    }

    static Class<? extends EventPublisher> serviceLocatorClass;

    public static <T extends EventPublisher> void setEventPublisher(Class<T> serviceLocator) {
        serviceLocatorClass = serviceLocator;
    }

    class DefaultEventEventPublisher implements EventPublisher {

        @Override
        public void publish(String no, String serialNo) {
            System.out.println("DefaultEventEventPublisher:" + serialNo);
        }
    }
}
