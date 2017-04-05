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

package org.sunyata.quark.descriptor;


import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.ContinueTypeEnum;
import org.sunyata.quark.basic.QuarkComponentOptions;
import org.sunyata.quark.basic.Validator;
import org.sunyata.quark.exception.ValidateException;

/**
 * Created by leo on 16/12/11.
 */
public class QuarkComponentDescriptor implements Validator {

    private ContinueTypeEnum continueType = ContinueTypeEnum.Succeed;
    private Class<? extends AbstractQuarkComponent> clazz = null;
    private QuarkComponentOptions options = new QuarkComponentOptions();
    private String businItemCode;
    private String businItemName;

    private Integer order;
    private Integer subOrder;

    public Integer getOrder() {
        return order;
    }

    public QuarkComponentDescriptor setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public Integer getSubOrder() {
        return subOrder;
    }

    public QuarkComponentDescriptor setSubOrder(Integer subOrder) {
        this.subOrder = subOrder;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public QuarkComponentDescriptor setVersion(String version) {
        this.version = version;
        return this;
    }

    private String version;

    public Class<? extends AbstractQuarkComponent> getClazz() {
        return clazz;
    }

    public QuarkComponentOptions getOptions() {
        return options;
    }

    public QuarkComponentDescriptor() {
    }

    public QuarkComponentDescriptor setClazz(Class<? extends AbstractQuarkComponent> clazz) {
        this.clazz = clazz;
        return this;
    }


    public QuarkComponentDescriptor setOptions(QuarkComponentOptions options) {
        this.options = options;
        return this;
    }


    public ContinueTypeEnum getContinueType() {
        return continueType;
    }

    public QuarkComponentDescriptor setContinueType(ContinueTypeEnum continueTypeEnum) {
        this.continueType = continueTypeEnum;
        return this;
    }


    public QuarkComponentDescriptor(Class<? extends AbstractQuarkComponent> clazz, QuarkComponentOptions options) {
        this.clazz = clazz;
        this.options = options;
    }

    @Override
    public void validate() {
        if (clazz == null) {
            throw new ValidateException("clazz must have a value");
        }
    }

    public QuarkComponentDescriptor setBusinItemCode(String businItemCode) {
        this.businItemCode = businItemCode;
        return this;
    }

    public String getBusinItemCode() {
        return businItemCode;
    }

    public QuarkComponentDescriptor setBusinItemName(String businItemName) {
        this.businItemName = businItemName;
        return this;
    }

    public String getBusinItemName() {
        return businItemName;
    }
}

