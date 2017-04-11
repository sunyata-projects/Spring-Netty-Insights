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

package org.sunyata.quark.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.BusinessInstanceFactory;
import org.sunyata.quark.basic.CanContinueTypeEnum;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.lock.BusinessLock;
import org.sunyata.quark.lock.BusinessLockService;
import org.sunyata.quark.publish.EventPublisher;
import org.sunyata.quark.store.BusinessInstanceStore;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.QuarkComponentLog;

import java.io.IOException;

/**
 * Created by leo on 16/12/14.
 */
public abstract class AbstractExecutor implements Executor {
    Logger logger = LoggerFactory.getLogger(AbstractExecutor.class);

//    @Override
//    public void run(BusinessContext businessContext) throws Exception {
//        ProcessResult result = execute(businessContext);
//        this.publishContinue(result, businessContext);
//    }

    protected ProcessResult execute(BusinessContext businessContext) throws Exception {
        ProcessResult result = ProcessResult.r();//未知
        try {
            // run
            result = businessContext.getBusinessComponent().run(businessContext);
        } catch (Exception ex) {
//            result.setProcessResultString(ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                if (result.getQuarkComponentInstance() != null) {
                    businessContext.getBusinessComponent().stateSync(businessContext, result);
                    writeLog(businessContext, result);
                } else {
                    logger.error("此业务不能被执行");
                }
            } catch (Exception ex) {
                //todo 写库失败后,要写入日志文件 lcl
                throw ex;
            }
            /*finally {
                // unlock serialNo
                lock.release();
            }*/
        }
        return result;
    }

    protected void publishContinue(ProcessResult result, BusinessContext businessContext) throws
            Exception {
        //publish next quarkComponent
        if (result.getQuarkComponentInstance() != null) {
            if (businessContext.getInstance().getCanContinue() == CanContinueTypeEnum.CanContinue) {
                EventPublisher.getPublisher().publish(businessContext.getSerialNo());
            }
        }
    }

    private void writeLog(BusinessContext businessContext, ProcessResult result) throws
            InstantiationException,
            IllegalAccessException, IOException {
        BusinessInstanceStore businessInstanceStore = ServiceLocator.getBestService(BusinessInstanceStore
                .class);
        QuarkComponentInstance quarkComponentInstance = result.getQuarkComponentInstance();
        QuarkComponentLog quarkComponentLog = BusinessInstanceFactory.createQuarkComponentLog(
                quarkComponentInstance.getBusinSerialNo(),
                quarkComponentInstance.getSerialNo(),
                quarkComponentInstance.getQuarkName(),
                quarkComponentInstance.getVersion(),
                quarkComponentInstance.getQuarkFriendlyName(),
                result.getProcessResultType(),
                "",
                result.getProcessResultString());

        businessInstanceStore.writeLog(businessContext.getInstance(), quarkComponentLog);
    }

    protected BusinessLock obtainBusinessLock(String path) throws Exception {
        BusinessLockService bestService = ServiceLocator.getBestService(BusinessLockService.class);
        return bestService.getLock("lock_" + path);
    }
}
