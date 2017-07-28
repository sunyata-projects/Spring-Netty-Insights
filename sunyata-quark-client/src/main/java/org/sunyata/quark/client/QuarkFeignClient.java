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

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
import org.sunyata.quark.client.dto.BusinessComponentInstance;

import java.util.List;

/**
 * Created by leo on 17/4/1.
 */
//@FeignClient(value = "quark-service", path = "business", fallback = QuarkFeignClient.QuarkFeignClientHystrix.class)
//@FeignClient(name = "quark-service",path = "business")
@Headers("Accept: application/json")
public interface QuarkFeignClient {
//    @RequestLine("POST")
//    @Headers("Content-Type: application/json")
//        //@RequestMapping(method = RequestMethod.POST, value = "/create")
//    JsonResponseResult create(@Param("serialNo") String serialNo, @Param("businName") String businName, @Param
//            ("parameterString") String parameterString, @Param("autoRun") boolean autoRun) throws Exception;

    @RequestLine("GET")
    @Headers("Content-Type: application/json")
        //@RequestMapping(method = RequestMethod.GET, value = "/components")
    JsonResponseResult<List<BusinessComponentDescriptor>> components() throws Exception;

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
        //@RequestMapping(method = RequestMethod.POST, value = "/run")
    JsonResponseResult run(@Param(value = "serialNo") String serialNo) throws Exception;

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
        //@RequestMapping(method = RequestMethod.POST, value = "/runByManual")
    JsonResponseResult runByManual(@Param(value = "serialNo") String serialNo, @Param(value =
            "quarkIndex") Integer quarkIndex, @Param(value = "parameterString") String
                                           parameters) throws Exception;

    @RequestLine("GET")
    @Headers("Content-Type: application/json")
        //@RequestMapping(method = RequestMethod.GET, value = "/instance")
    JsonResponseResult<BusinessComponentInstance> instance(@Param(value = "serialNo") String serialNo) throws Exception;

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
        //@RequestMapping(method = RequestMethod.POST, value = "/create")
    JsonResponseResult create(@Param("serialNo") String serialNo,
                              @Param("businName") String businName,
                              @Param("sponsor") String sponsor,
                              @Param("relationId") String relationId,
                              @Param("parameterString") String parameterString,
                              @Param("autoRun") boolean autoRun)
            throws Exception;
}
