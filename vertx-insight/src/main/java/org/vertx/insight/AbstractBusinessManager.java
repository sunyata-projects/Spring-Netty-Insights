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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.AbstractBusinessComponent;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.exception.CanNotFindAnnotationException;
import org.sunyata.quark.ioc.DefaultServiceLocatorFactory;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.publish.DefaultEventPublisherFactory;
import org.sunyata.quark.publish.EventPublisher;
import org.sunyata.quark.stereotype.BusinessComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractBusinessManager implements BusinessManager {


    Logger logger = LoggerFactory.getLogger(AbstractBusinessManager.class);

    private static ConcurrentMap<String, Class<? extends AbstractBusinessComponent>> businessComponentMaps = new
            ConcurrentHashMap<>();
    private Collection<Object> businessComponents;

    @Override
    public void initialize(Collection<Object> businessComponents) throws Exception {
        this.businessComponents = businessComponents;
        initializeComponent();
    }

    protected void initializeComponent() throws Exception {
        for (Object businessComponent : businessComponents) {
            Class businessComponentClass = businessComponent.getClass();
            if (businessComponentClass != null) {
                register(businessComponentClass);
            }
        }
    }

    public AbstractBusinessComponent getBusinessComponent(String businName) throws IllegalAccessException,
            InstantiationException {
        Class<? extends AbstractBusinessComponent> orDefault = businessComponentMaps.getOrDefault(businName, null);
        if (orDefault != null) {
            AbstractBusinessComponent service = ServiceLocator.getLocator().getService(businessComponentMaps
                    .getOrDefault(businName,
                            null));
            return service;
        }
        return null;
    }

    @Override
    public <T extends AbstractBusinessComponent> void register(Class<T> businessComponentClass) throws Exception {
        BusinessComponent annotation = businessComponentClass.getAnnotation(BusinessComponent.class);
        if (annotation == null) {
            logger.error("The business component '{}' annotation is not defined", businessComponentClass.getName());
            throw new CanNotFindAnnotationException("The business component annotation is not defined");
        }
        if (businessComponentMaps.getOrDefault(annotation.businName(), null) == null) {
            logger.info("register business component {}", businessComponentClass.getName());
            businessComponentMaps.put(annotation.businName(), businessComponentClass);
        }
    }


    @Override
    public <T extends ServiceLocator> void setServiceLocator(Class<T> serviceLocator) {
        DefaultServiceLocatorFactory.setServiceLocator(serviceLocator);
    }

    @Override
    public <T extends EventPublisher> void setEventPublisher(Class<T> eventPublisherClass) {
        DefaultEventPublisherFactory.setEventPublisher(eventPublisherClass);
    }

    @Override
    public List<BusinessComponentDescriptor> getComponents() throws Exception {
        List<BusinessComponentDescriptor> results = new ArrayList<>();
        for (Class<? extends AbstractBusinessComponent> cls : businessComponentMaps.values()) {
            AbstractBusinessComponent service = ServiceLocator.getLocator().getService(cls);
            results.add(service.getBusinessComponentDescriptor());
        }
        return results;
    }
}
