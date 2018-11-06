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

package org.vertx.insight.descriptor;

/**
 * Created by leo on 16/12/11.
 */
public class BusinessComponentDescriptor {

    private String description;
    private String version;
    private String businName;//业务标识符ccop.withdraw
    private String bisinFriendlyName;//业务名称
    private boolean compensationSwitch;

    public boolean getCompensationSwitch() {
        return compensationSwitch;
    }

    public BusinessComponentDescriptor setCompensationSwitch(boolean compensationSwitch) {
        this.compensationSwitch = compensationSwitch;
        return this;
    }



    public BusinessComponentDescriptor setBusinName(String businName) {
        this.businName = businName;
        return this;
    }

    public BusinessComponentDescriptor setBisinFriendlyName(String bisinFriendlyName) {
        this.bisinFriendlyName = bisinFriendlyName;
        return this;
    }



    public String getDescription() {
        return description;
    }

    public BusinessComponentDescriptor setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public BusinessComponentDescriptor setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getBusinName() {
        return businName;
    }

    public String getBisinFriendlyName() {
        return bisinFriendlyName;
    }


}
