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

import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.BusinessComponentInstance;

import java.util.HashMap;
import java.util.List;

/**
 * Created by leo on 17/4/1.
 */


public interface QuarkClient {


    JsonResponseResult create(String serialNo, String businName, String parameterString);

    JsonResponseResult create(String serialNo, String businName, HashMap<String, Object> parameters);

    JsonResponseResult<List<BusinessComponentDescriptor>> components();


    JsonResponseResult run(String serialNo);


    JsonResponseResult<BusinessComponentInstance> instance(String serialNo);
}
