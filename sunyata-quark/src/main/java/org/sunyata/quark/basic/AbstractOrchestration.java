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

package org.sunyata.quark.basic;

import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.descriptor.MutltipleQuarkComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.ioc.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 16/12/14.
 */
public abstract class AbstractOrchestration<T extends Flow> implements Orchestration<T> {

    private BusinessComponentDescriptor businessComponentDescriptor;
    private ProcessSequencing processSequencing;

    @Override
    public <T extends Flow> T orchestrate(Class<T> tClass) {
        try {
            T service = ServiceLocator.getLocator().getService(tClass);
            service.setProcessSequencing(this.processSequencing);
            service.setBusinessComponentDescriptor(this.businessComponentDescriptor);
            service.setOrchestration(this);
            return service;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public AbstractOrchestration<T> setBusinessComponentDescriptor(BusinessComponentDescriptor descriptor) {
        this.businessComponentDescriptor = descriptor;
        return this;
    }


    public AbstractOrchestration() {
        processSequencing = new ProcessSequencing();
    }

    public void validate() {

    }

    public AbstractOrchestration<T> beginWith(Class<? extends AbstractQuarkComponent> clazz) {
        processSequencing.clear();
        return add(new QuarkComponentDescriptor().setClazz(clazz).setOptions(new QuarkComponentOptions()));
    }

    public AbstractOrchestration<T> beginWith(QuarkComponentDescriptor melodiesMeta) {
        processSequencing.clear();
        return add(melodiesMeta);
    }

    private AbstractOrchestration<T> add(QuarkComponentDescriptor melodiesMeta) {
        MutltipleQuarkComponentDescriptor add = new MutltipleQuarkComponentDescriptor().add(melodiesMeta);
        add(add);
        return this;
    }

    private AbstractOrchestration<T> add(MutltipleQuarkComponentDescriptor descriptor) {
        Integer order = processSequencing.size();
        Integer subOrder = 0;
        //descriptor.setOrderby(++order);
        order++;
        descriptor.setOrder(order);
        for (QuarkComponentDescriptor desc : descriptor.getItems()) {
            desc.setOrder(order);
            desc.setSubOrder(++subOrder);
        }
        processSequencing.add(descriptor);
        return this;
    }

    public AbstractOrchestration<T> beginWith(Class<? extends AbstractQuarkComponent> clazz, QuarkComponentOptions
            options) {
        processSequencing.clear();
        return add(new QuarkComponentDescriptor().setClazz(clazz).setOptions(options));
    }

    public AbstractOrchestration<T> next(Class<? extends AbstractQuarkComponent> clazz) {
        return add(new QuarkComponentDescriptor().setClazz(clazz));
    }

    public AbstractOrchestration<T> next(QuarkComponentDescriptor melodiesMeta) {
        return add(melodiesMeta);
    }

    public AbstractOrchestration<T> next(Class<? extends AbstractQuarkComponent> clazz, QuarkComponentOptions
            options) {
        return add(new QuarkComponentDescriptor().setClazz(clazz).setOptions(options));
    }

    public AbstractOrchestration<T> parallel(MutltipleQuarkComponentDescriptor melodiesMeta) {
        return add(melodiesMeta);
    }

    public AbstractOrchestration<T> parallel(QuarkComponentDescriptor... melodiesMeta) {
        List<QuarkComponentDescriptor> items = new ArrayList<>();
        for (QuarkComponentDescriptor descriptor : melodiesMeta) {
            items.add(descriptor);
        }
        return add(new MutltipleQuarkComponentDescriptor().setItems(items));
    }


    private void setLastNodeQuarkContinueType(ContinueTypeEnum type) {
        MutltipleQuarkComponentDescriptor lastOne = processSequencing.getLastOne();
        if (lastOne != null) {
            lastOne.getItems().forEach(p -> p.setContinueType(type));
        }
    }

    public AbstractOrchestration<T> succeed() {
        setLastNodeQuarkContinueType(ContinueTypeEnum.Succeed);
        return this;
    }

    public AbstractOrchestration<T> anyway() {
        setLastNodeQuarkContinueType(ContinueTypeEnum.Anyway);
        return this;
    }


}


