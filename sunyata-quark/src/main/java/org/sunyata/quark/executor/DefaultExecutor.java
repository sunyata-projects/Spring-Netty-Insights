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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.exception.CanNotExecuteException;

/**
 * Created by leo on 16/12/14.
 */
public class DefaultExecutor extends AbstractExecutor {
    Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

    @Override
    public ProcessResult run(BusinessContext businessContext) throws Exception {
        ProcessResult result = ProcessResult.r();
        try {
//            logger.info("Exector开始执行,SerianNo:" + businessContext.getSerialNo());
            result = execute(businessContext);
//            logger.info("Exector执行完毕,SerianNo:" + businessContext.getSerialNo() + ",Result:" + Json.encode(result));
        } catch (CanNotExecuteException canNotEx) {
            //logger.error(canNotEx.getMessage());
        } catch (Throwable ex) {
            logger.error("An error occurred while executing business component,SerialNo:{},error message:{}",
                    businessContext.getSerialNo(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            //lock.release();
        }
//        if (result.getProcessResultType() == ProcessResultTypeEnum.S) {
//            //this.publishContinue(result, businessContext);
//        }
        return result;
    }
}
