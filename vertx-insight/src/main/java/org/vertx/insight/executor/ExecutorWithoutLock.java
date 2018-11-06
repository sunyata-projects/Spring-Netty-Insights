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

package org.vertx.insight.executor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;

/**
 * Created by leo on 17/3/27.
 */
public class ExecutorWithoutLock extends AbstractExecutor {
    Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

    @Override
    public ProcessResult run(BusinessContext businessContext) throws Exception {
        ProcessResult result = ProcessResult.r();
        try {
            result = execute(businessContext);
        } catch (Throwable ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
        //this.publishContinue(businessContext.getSerialNo());
        return result;
    }
}
