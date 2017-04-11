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

package org.sunyata.quark.provider.springcloud.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.provider.springcloud.SpringContextUtil;

/**
 * Created by leo on 17/3/30.
 */
@RestController
@RequestMapping("quark")
public class QuarkController {

    Logger logger = LoggerFactory.getLogger(QuarkController.class);

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public ProcessResult run(@RequestBody BusinessContext businessContext) throws Exception {
        String quarkName = null;
        try {
            quarkName = (String) businessContext.getCurrentQuarkDescriptor().getOptions().getValue
                    ("quark-name", null);
            if (quarkName == null) {
                String format = String.format("quark名称不能为空,流水号:%s", businessContext.getCurrentQuarkSerialNo());
                logger.error(format);
                return ProcessResult.e().setProcessResultString(format);
            }
            AbstractQuarkComponent bean = (AbstractQuarkComponent) SpringContextUtil.getBean(quarkName,
                    AbstractQuarkComponent.class);
            ProcessResult run = bean.run(businessContext);
            logger.info(String.format("业务执行完毕,流水号为:%s", businessContext.getCurrentQuarkSerialNo()));
            return run;

        } catch (BeansException beanException) {
            String logString = String.format("没有找到名称为%s的quark", quarkName);
            logger.error(logString);
            return ProcessResult.e().setProcessResultString(logString);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            return ProcessResult.e().setProcessResultString(ExceptionUtils.getStackTrace(ex));
        }
    }
}
