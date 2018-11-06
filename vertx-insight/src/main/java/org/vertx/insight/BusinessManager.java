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

package org.vertx.insight;

import org.sunyata.quark.basic.AbstractBusinessComponent;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.publish.EventPublisher;

import java.util.Collection;
import java.util.List;

/**
 * Created by leo on 16/12/14.
 */
public interface BusinessManager {

    void initialize(Collection<Object> names) throws Exception;

    <T extends AbstractBusinessComponent> void register(Class<T> businessComponent) throws
            Exception;

    <T extends ServiceLocator> void setServiceLocator(Class<T> serviceLocator);

    <T extends EventPublisher> void setEventPublisher(Class<T> eventPublisherClass);

    List<BusinessComponentDescriptor> getComponents() throws Exception;

    AbstractBusinessComponent getBusinessComponent(String businName) throws IllegalAccessException,
            InstantiationException;
}
