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

package org.sunyata.quark.basic;

/**
 * Created by leo on 16/12/14.
 */
public abstract class AbstractQuarkComponent<TParameterInfo extends QuarkParameterInfo> {

    public TParameterInfo getParameterInfo(BusinessContext context) throws Exception {
        return (TParameterInfo) new QuarkParameterInfo().setBusinessContext(context);
    }

    public AbstractQuarkComponent() {

    }

    public ProcessResult run(BusinessContext context) throws Exception {
        ProcessResult processResult = ProcessResult.r();
//        QuarkComponentDescriptor currentQuarkDescriptor = context.getCurrentQuarkDescriptor();
        QuarkParameterInfo parameterInfo = getParameterInfo(context);
        if (parameterInfo == null) {
            parameterInfo = new QuarkParameterInfo().setBusinessContext(context);
        }
        parameterInfo.setBusinessContext(context);
        if (context.getInstance().getBusinessMode() == BusinessModeTypeEnum.Normal) {
            processResult = execute((TParameterInfo) parameterInfo);
        } else {
            processResult = compensate((TParameterInfo) parameterInfo);
        }
        return processResult;
    }

    protected abstract ProcessResult execute(TParameterInfo parameterInfo);

    protected abstract ProcessResult compensate(TParameterInfo parameterInfo);
}
