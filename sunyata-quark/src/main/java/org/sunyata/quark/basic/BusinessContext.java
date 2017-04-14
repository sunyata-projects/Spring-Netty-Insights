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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.BusinessComponentInstance;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by leo on 16/12/14.
 */
@JsonSerialize()
public class BusinessContext implements Serializable {

    private AbstractBusinessComponent businessComponent;
    private BusinessModeTypeEnum businessMode;
    private boolean primary;
    private ConcurrentMap<String, Object> parameters = new ConcurrentHashMap<>();
    private String serialNo;
    private BusinessComponentInstance instance;
    private QuarkComponentDescriptor currentQuarkDescriptor;
    private String currentQuarkSerialNo;

    public BusinessSerializableContext generateSerializableContext() {
        BusinessSerializableContext context = new BusinessSerializableContext();
        context.setBusinessMode(this.getBusinessMode());
        context.setPrimary(this.isPrimary());
        context.setParameters(this.getParameters());
        context.setSerialNo(this.getSerialNo());
        context.setInstance(this.getInstance());
        context.setCurrentQuarkDescriptor(this.getCurrentQuarkDescriptor().setClazz(null));
        context.setCurrentQuarkSerialNo(this.getCurrentQuarkSerialNo());
        return context;
    }

    public static BusinessContext getContext(String serialNo, AbstractBusinessComponent abstractBusinessComponent,
                                             BusinessComponentInstance instance)
            throws IllegalAccessException, InstantiationException {

        BusinessContext businessContext = new BusinessContext().setInstance(instance).setBusinessComponent
                (abstractBusinessComponent).setSerialNo
                (serialNo);
        return businessContext;
    }

    public BusinessModeTypeEnum getBusinessMode() {
        return businessMode;
    }

    public BusinessContext setBusinessMode(BusinessModeTypeEnum businessMode) {
        this.businessMode = businessMode;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public BusinessContext setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public AbstractBusinessComponent getBusinessComponent() {
        return businessComponent;
    }

    public BusinessContext setBusinessComponent(AbstractBusinessComponent value) {
        this.businessComponent = value;
        return this;
    }

    public BusinessContext setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public BusinessContext setInstance(BusinessComponentInstance instance) {
        this.instance = instance;
        return this;
    }

    public BusinessComponentInstance getInstance() {
        return instance;
    }

    public void setCurrentQuarkDescriptor(QuarkComponentDescriptor currentQuarkDescriptor) {
        this.currentQuarkDescriptor = currentQuarkDescriptor;
    }

    public QuarkComponentDescriptor getCurrentQuarkDescriptor() {
        return currentQuarkDescriptor;
    }

    public ConcurrentMap<String, Object> getParameters() {
        return parameters;
    }

    public BusinessContext setParameters(ConcurrentMap<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    public void setCurrentQuarkSerialNo(String currentQuarkSerialNo) {
        this.currentQuarkSerialNo = currentQuarkSerialNo;
    }

    public String getCurrentQuarkSerialNo() {
        return currentQuarkSerialNo;
    }
}
