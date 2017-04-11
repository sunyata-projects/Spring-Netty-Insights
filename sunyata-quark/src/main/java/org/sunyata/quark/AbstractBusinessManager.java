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

package org.sunyata.quark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.*;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.ioc.ScanFilter;
import org.sunyata.quark.ioc.DefaultServiceLocatorFactory;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.json.JsonObject;
import org.sunyata.quark.publish.DefaultEventPublisherFactory;
import org.sunyata.quark.publish.EventPublisher;
import org.sunyata.quark.stereotype.BusinessComponent;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.BusinessInstanceLoader;
import org.sunyata.quark.store.BusinessInstanceStore;
import org.sunyata.quark.util.PackageUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by leo on 16/12/14.
 */
public abstract class AbstractBusinessManager implements BusinessManager {


    Logger logger = LoggerFactory.getLogger(AbstractBusinessManager.class);

    private static ConcurrentMap<String, Class<? extends AbstractBusinessComponent>> maps = new ConcurrentHashMap<>();
    private String scanPackage;

    @Override
    public void setScanPackage(String scanPackages) {
        this.scanPackage = scanPackages;
    }


    @Override
    public void initialize() throws Exception {
        initializeComponent();
    }

    protected void initializeComponent() throws Exception {
        if (this.scanPackage == null || this.scanPackage.trim().length() == 0) {
            throw new Exception("please set scan");
        }
        ScanFilter filter = clazz -> {
            Annotation annotation = clazz.getAnnotation(BusinessComponent.class);
            return annotation != null;
        };
        List<Class> classes = PackageUtils.scan(scanPackage, filter);
        for (Class clazz : classes) {
            register(clazz);
        }
    }

    @Override
    public void create(String serialNo, String businName, String parameterString) throws Exception {
        if (parameterString == null || parameterString.trim().length() == 0) {
            throw new Exception("参数不能为空");
        }
        try {
            new JsonObject(parameterString);
        } catch (Exception ex) {
            throw ex;
        }
//        long startTime = System.currentTimeMillis();   //获取开始时间
        BusinessInstanceStore bestService = ServiceLocator.getBestService(BusinessInstanceStore.class);
        AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(businName);
        BusinessComponentInstance instance = BusinessInstanceFactory.createInstance(
                serialNo,
                parameterString,
                abstractBusinessComponent);
//        long endTime = System.currentTimeMillis();   //获取开始时间
//        logger.info("创建实例时间:"+String.valueOf(endTime - startTime) + "ms");
        bestService.create(instance);
    }

    public void run(String serialNo) throws Exception {
        BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
        BusinessComponentInstance instance = bestService.load(serialNo);
        if (instance == null) {
            throw new Exception("businessInstance  cannot be found");
        }
        instance.readOriginalHashCode();
        AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
        if (abstractBusinessComponent == null) {
            throw new Exception("没有找到组件");
        }

        BusinessContext context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);
        context.setPrimary(true);
        context.setBusinessMode(instance.getBusinessMode());
        abstractBusinessComponent.getExecutor().run(context);
    }

    @Override
    public void retry(String serialNo) throws Exception {
        BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
        BusinessComponentInstance instance = bestService.load(serialNo);
        instance.readOriginalHashCode();
        AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
        if (abstractBusinessComponent == null) {
            throw new Exception("没有找到组件");
        }

        BusinessContext context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);
        context.setPrimary(false);
        context.setBusinessMode(instance.getBusinessMode());
        abstractBusinessComponent.getExecutor().run(context);
    }

    public void retry() throws Exception {
        BusinessQueryService bestService = ServiceLocator.getBestService(BusinessQueryService.class);
        List<BusinessComponentInstance> topNWillRetryBusiness = bestService.findTopNWillRetryBusiness(10);

        for (BusinessComponentInstance instance : topNWillRetryBusiness) {
            //instance.setItems(bestService.findQuarkComponentInstances(instance.getSerialNo()));
            retry(instance.getSerialNo());
        }

    }

    public void compensate(String serialNo) throws Exception {
        BusinessInstanceLoader bestService = ServiceLocator.getBestService(BusinessInstanceLoader.class);
        BusinessComponentInstance instance = bestService.load(serialNo);
        if (instance.getBusinessMode() != BusinessModeTypeEnum.Compensation) {
            throw new Exception("此业务不可补偿");
        }
        instance.readOriginalHashCode();
        AbstractBusinessComponent abstractBusinessComponent = getBusinessComponent(instance.getBusinName());
        if (abstractBusinessComponent == null) {
            throw new Exception("没有找到组件");
        }

        BusinessContext context = BusinessContext.getContext(serialNo, abstractBusinessComponent, instance);

        abstractBusinessComponent.getExecutor().run(context);
    }

    private AbstractBusinessComponent getBusinessComponent(String businName) throws IllegalAccessException,
            InstantiationException {
        Class<? extends AbstractBusinessComponent> orDefault = maps.getOrDefault(businName, null);
        if (orDefault != null) {
            AbstractBusinessComponent service = ServiceLocator.getLocator().getService(maps.getOrDefault(businName,
                    null));
            return service;
        }
        return null;
    }

    @Override
    public <T extends AbstractBusinessComponent> void register(Class<T> businessComponentClazz) throws Exception {
        BusinessComponent annotation = businessComponentClazz.getAnnotation(BusinessComponent.class);
        if (annotation == null) {
            throw new Exception("业务组件没有定义标注");
        }
        maps.put(annotation.businName(), businessComponentClazz);
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
        for (Class<? extends AbstractBusinessComponent> cls : maps.values()) {
            AbstractBusinessComponent service = ServiceLocator.getLocator().getService(cls);
            results.add(service.getBusinessComponentDescriptor());
        }
        return results;
    }
}
