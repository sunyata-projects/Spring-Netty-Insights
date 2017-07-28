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

import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.BusinessComponentInstance;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by leo on 16/12/14.
 */
public class BusinessSerializableContext implements Serializable {

    private BusinessModeTypeEnum businessMode;
    private boolean primary;
    private ConcurrentMap<String, Object> parameters = new ConcurrentHashMap<>();
    private String serialNo;
    private BusinessComponentInstance instance;
    private QuarkComponentDescriptor currentQuarkDescriptor;
    private String currentQuarkSerialNo;
    private String quarkServiceName;


    public static BusinessSerializableContext getContext(String serialNo, AbstractBusinessComponent
            abstractBusinessComponent,
                                                         BusinessComponentInstance instance)
            throws IllegalAccessException, InstantiationException {

        return new BusinessSerializableContext().setInstance(instance).setSerialNo(serialNo);
    }

    public BusinessModeTypeEnum getBusinessMode() {
        return businessMode;
    }

    public BusinessSerializableContext setBusinessMode(BusinessModeTypeEnum businessMode) {
        this.businessMode = businessMode;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public BusinessSerializableContext setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public BusinessSerializableContext setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public BusinessSerializableContext setInstance(BusinessComponentInstance instance) {
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

    //public ConcurrentMap<String, Object> getParameters() {
    //return parameters;
    //}

//    public BusinessSerializableContext setParameters(ConcurrentMap<String, Object> parameters) {
//        this.parameters = parameters;
//        return this;
//    }

    public void setCurrentQuarkSerialNo(String currentQuarkSerialNo) {
        this.currentQuarkSerialNo = currentQuarkSerialNo;
    }

    public String getCurrentQuarkSerialNo() {
        return currentQuarkSerialNo;
    }

    public void setQuarkServiceName(String quarkServiceName) {
        this.quarkServiceName = quarkServiceName;
    }

    public String getQuarkServiceName() {
        return quarkServiceName;
    }
}
