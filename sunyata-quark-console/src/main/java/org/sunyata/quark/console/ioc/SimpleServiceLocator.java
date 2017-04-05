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

package org.sunyata.quark.console.ioc;

import org.sunyata.quark.ioc.BeansException;
import org.sunyata.quark.ioc.ScanFilter;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.util.ClassUtils;
import org.sunyata.quark.util.PackageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leo on 17/3/15.
 */
public class SimpleServiceLocator implements ServiceLocator {

    @Override
    public <T> T getService(Class<T> requiredType) throws BeansException, IllegalAccessException,
            InstantiationException {
        //return SpringContextUtil.getBean(requiredType);
        return requiredType.newInstance();
    }

    @Override
    public <T> Map<String, T> getServiceOfType(Class<T> type) throws BeansException, IllegalAccessException,
            InstantiationException {
//        return SpringContextUtil.getApplicationContext().getBeansOfType(type);
        List<Class> classes = PackageUtils.scan("org.sunyata.quark.console", new ScanFilter() {
            @Override
            public boolean accept(Class clazz) {
                if (ClassUtils.isAssignable(type, clazz)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        Map<String, T> results = new HashMap<>();
        for (Class cls : classes) {
            Object o = cls.newInstance();
            results.put(cls.getName(), (T) o);
        }
        return results;
    }
}
