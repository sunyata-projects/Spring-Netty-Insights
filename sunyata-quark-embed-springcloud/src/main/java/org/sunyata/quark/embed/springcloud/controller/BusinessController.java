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

package org.sunyata.quark.embed.springcloud.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sunyata.quark.BusinessManager;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.embed.springcloud.JsonResponseResult;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.BusinessInstanceLoader;

import java.io.IOException;
import java.util.List;

/**
 * Created by leo on 17/3/30.
 */
@RestController
@RequestMapping("business")
public class BusinessController {

    Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    BusinessManager businessManager;

    @RequestMapping(value = "/components", method = RequestMethod.GET)
    public JsonResponseResult components() throws Exception {
        try {
            List<BusinessComponentDescriptor> componentDescriptors = businessManager.getComponents();
            return JsonResponseResult.Success().setResponse(componentDescriptors);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public JsonResponseResult create(String serialNo, String businName, String parameterString) throws Exception {
        try {
            businessManager.create(serialNo, businName, parameterString);
            return JsonResponseResult.Success();
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public JsonResponseResult run(String serialNo) throws Exception {
        try {
            businessManager.run(serialNo);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
        return JsonResponseResult.Success();
    }

//    @RequestMapping(value = "/asyncrun", method = RequestMethod.POST)
//    public JsonResponseResult asyncRun(String serialNo) throws Exception {
//        businessManager.asyncRun(serialNo);
//        return JsonResponseResult.Success();
//    }

    @Autowired
    BusinessInstanceLoader businessQueryService;

    @RequestMapping(value = "instance", method = RequestMethod.GET)
    public JsonResponseResult getInstance(String serialNo) throws IOException {
        BusinessComponentInstance businessComponentInstance = businessQueryService.load(serialNo);
        try {
            return JsonResponseResult.Success().setResponse(businessComponentInstance);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }
}
