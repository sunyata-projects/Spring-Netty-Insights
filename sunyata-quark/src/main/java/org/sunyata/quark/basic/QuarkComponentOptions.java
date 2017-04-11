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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leo on 16/12/11.
 */
public class QuarkComponentOptions implements Serializable {
    private Map<String, Object> maps = new HashMap<>();

    public QuarkComponentOptions put(String key, Object value) {
        maps.put(key, value);
        return this;
    }

    public Object getValue(String key, String defaultValue) {
        return maps.getOrDefault(key, defaultValue);
    }

    public QuarkComponentOptions() {
        retryLimitTimes = 3;
        canCancel = true;
    }

    private boolean canCancel;

    public Integer getRetryLimitTimes() {
        return retryLimitTimes;
    }

    public QuarkComponentOptions setRetryLimitTimes(Integer retryLimitTimes) {
        this.retryLimitTimes = retryLimitTimes;
        return this;
    }

    private Integer retryLimitTimes;//错误后,重试次数

    public QuarkComponentOptions setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        return this;
    }

    public boolean isCanCancel() {
        return canCancel;
    }
}
