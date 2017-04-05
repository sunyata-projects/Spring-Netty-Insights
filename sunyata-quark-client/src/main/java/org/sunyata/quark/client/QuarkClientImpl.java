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

package org.sunyata.quark.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.BusinessComponentInstance;

import java.util.List;

/**
 * Created by leo on 17/4/1.
 */
@Component
public class QuarkClientImpl implements QuarkClient {
    @Autowired(required = false)
    QuarkFeignClient quarkFeignClient;

    public JsonResponseResult create(String serialNo, String businCode, String parameterString) {
        return quarkFeignClient.create(serialNo, businCode, parameterString);
    }

    public JsonResponseResult<List<BusinessComponentDescriptor>> components() {
        return quarkFeignClient.components();
    }

    public JsonResponseResult run(String serialNo) {
        return quarkFeignClient.run(serialNo);
    }

    public JsonResponseResult<BusinessComponentInstance> instance(String serialNo) {
        return quarkFeignClient.instance(serialNo);
    }
}
