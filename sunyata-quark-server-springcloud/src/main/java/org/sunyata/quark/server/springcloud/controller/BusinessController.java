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

package org.sunyata.quark.server.springcloud.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyata.quark.BusinessManager;
import org.sunyata.quark.MessageQueueService;
import org.sunyata.quark.QuarkExecutor;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.ProcessResultTypeEnum;
import org.sunyata.quark.basic.QuarkNotifyInfo;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.message.CreateBusinessComponentMessageInfo;
import org.sunyata.quark.message.RunByManualMessageInfo;
import org.sunyata.quark.server.springcloud.JsonResponseResult;
import org.sunyata.quark.server.springcloud.QuarkServerProperties;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.BusinessInstanceStore;

import java.io.IOException;
import java.util.List;

/**
 * Created by leo on 17/3/30.
 */
@RestController
@RequestMapping("business")
public class BusinessController {

    Logger logger = LoggerFactory.getLogger(BusinessController.class);

//    @Autowired
//    @Qualifier("asyncBusinessManager")
//    BusinessManager asyncbusinessManager;

    @Autowired
    BusinessManager syncBusinessManager;

    @Autowired
    QuarkExecutor quarkExecutor;

    @Autowired
    MessageQueueService messageQueueService;


    @RequestMapping(value = "/components", method = RequestMethod.GET)
    public JsonResponseResult components() throws Exception {
        try {
            List<BusinessComponentDescriptor> componentDescriptors = syncBusinessManager.getComponents();
            return JsonResponseResult.Success().setResponse(componentDescriptors);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST, headers = {"content-type=application/json"})
    public JsonResponseResult create(@RequestBody CreateBusinessComponentMessageInfo info) throws
            Exception {
        BusinessComponentInstance instance = null;
        try {
            instance = quarkExecutor.create(info.getSerialNo(), info.getBusinName(), info
                    .getSponsor(), info.getRelationId
                    (), info.getParameterString());
            if (instance != null && info.isAutoRun()) {
                messageQueueService.enQueue(info.getBusinName(), info.getBusinName(), 0, info.getSerialNo(), true);
            }
            return JsonResponseResult.Success();
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }

    @RequestMapping(value = "/run/{serialNo}", method = RequestMethod.POST)
    public JsonResponseResult run(@PathVariable String serialNo) throws Exception {
        try {
            quarkExecutor.run(serialNo);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
        return JsonResponseResult.Success();
    }


    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public JsonResponseResult notify(@RequestBody QuarkNotifyInfo quarkNotifyInfo) throws
            Exception {
        try {
            ProcessResult result = quarkNotifyInfo.getProcessResult();
            int delay = 0;
            if (result.getProcessResultType() != ProcessResultTypeEnum.S) {
                result.setManualReducePriority(3);
                delay = 15000;
            }
            messageQueueService.enQueue("Notify", "Notify", delay, quarkNotifyInfo.getSerialNo(),
                    quarkNotifyInfo.getQuarkIndex(),
                    result);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
        return JsonResponseResult.Success();
    }

    @RequestMapping(value = "/runByManual", method = RequestMethod.POST)
    public JsonResponseResult runByManual(@RequestBody RunByManualMessageInfo runByManualMessageInfo)
            throws Exception {
        try {
            ProcessResult result = quarkExecutor.runByManual(runByManualMessageInfo.getSerialNo(),
                    runByManualMessageInfo.getQuarkIndex(), runByManualMessageInfo.getParameterString());
            ProcessResultTypeEnum processResultType = result.getProcessResultType();
            if (processResultType == ProcessResultTypeEnum.S) {
                return JsonResponseResult.Success().setResponse(result.getBody());
            } else {
                return JsonResponseResult.Error(99, result.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }

    @Autowired
    BusinessInstanceStore businessInstanceStoreService;

    @RequestMapping(value = "instance", method = RequestMethod.GET)
    public JsonResponseResult getInstance(String serialNo) throws IOException {
        BusinessComponentInstance businessComponentInstance = businessInstanceStoreService.load(serialNo);
        try {
            return JsonResponseResult.Success().setResponse(businessComponentInstance);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }
}
