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

package org.vertx.insight.store;

import java.io.Serializable;

/**
 * Created by leo on 16/12/11.
 */
public class QuarkParameter implements Serializable {
    public QuarkParameter() {

    }

    public Long getId() {
        return id;
    }

    public int getParameterType() {
        return parameterType;
    }

    public QuarkParameter setParameterType(int parameterType) {
        this.parameterType = parameterType;
        return this;
    }

    public int parameterType;

    public String getBusinessSerialNo() {
        return businessSerialNo;
    }

    public QuarkParameter setBusinessSerialNo(String businessSerialNo) {
        this.businessSerialNo = businessSerialNo;
        return this;
    }

    public String businessSerialNo;

    public QuarkParameter setId(Long id) {
        this.id = id;
        return this;
    }

    public String getParameter() {
        return parameter;
    }

    public QuarkParameter setParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    private Long id;
    private String parameter;

}
