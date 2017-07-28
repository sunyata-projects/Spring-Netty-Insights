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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.sunyata.quark.BusinessManager;
import org.sunyata.quark.NotifyRunCommand;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.ProcessResultTypeEnum;
import org.sunyata.quark.basic.QuarkNotifyInfo;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.message.CreateBusinessComponentMessageInfo;
import org.sunyata.quark.message.RunByManualMessageInfo;
import org.sunyata.quark.server.springcloud.JsonResponseResult;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.BusinessInstanceLoader;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by leo on 17/3/30.
 */
@RestController
@RequestMapping("business")
public class BusinessController {

    Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    @Qualifier("asyncBusinessManager")
    BusinessManager asyncbusinessManager;

    @Autowired
    BusinessManager syncBusinessManager;

    @RequestMapping(value = "/components", method = RequestMethod.GET)
    public JsonResponseResult components() throws Exception {
        try {
            List<BusinessComponentDescriptor> componentDescriptors = asyncbusinessManager.getComponents();
            return JsonResponseResult.Success().setResponse(componentDescriptors);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return JsonResponseResult.Error(99, ExceptionUtils.getMessage(ex));
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, headers = {"content-type=application/json"})
    public JsonResponseResult create(@RequestBody CreateBusinessComponentMessageInfo info) throws
            Exception {
        try {
            syncBusinessManager.create(info.getSerialNo(), info.getBusinName(), info.getSponsor(), info.getRelationId
                    (), info.getParameterString());
            if (info.isAutoRun()) {
                asyncbusinessManager.run(info.getSerialNo());
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
            asyncbusinessManager.run(serialNo);
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
            //asyncbusinessManager.quarkNotify(quarkNotifyInfo.getSerialNo(), quarkNotifyInfo.getQuarkIndex(), result);
           new NotifyRunCommand(syncBusinessManager,quarkNotifyInfo.getSerialNo(),quarkNotifyInfo.getQuarkIndex(),
                    result).queue();
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
            ProcessResult result = syncBusinessManager.runByManual(runByManualMessageInfo.getSerialNo(),
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

    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public JsonResponseResult test() throws IOException {
        getAsyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    logger.info(String.valueOf(i));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return JsonResponseResult.Success();
    }

}
