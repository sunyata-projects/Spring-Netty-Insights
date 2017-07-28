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

package org.sunyata.quark.embed.demo.springcloud.components;

import org.springframework.stereotype.Component;
import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkParameterInfo;
import org.sunyata.quark.stereotype.QuarkComponent;

/**
 * Created by leo on 16/12/15.
 */
@Component
@QuarkComponent(quarkName = "SuccessQuarkComponent4", quarkFriendlyName = "SuccessQuarkComponent4", version = "1.0")
public class SuccessQuarkComponent4 extends AbstractQuarkComponent {

    public SuccessQuarkComponent4() {
    }

    @Override
    public QuarkParameterInfo getParameterInfo(BusinessContext context) throws Exception {
        return null;
    }

    @Override
    public ProcessResult execute(QuarkParameterInfo parameterInfo) {
        System.out.println("hello world" + Thread.currentThread().getName());
        Object parameter = parameterInfo.getBusinessContext().getParameter("key1", "defaultValue");
        System.out.println(parameter);
        return ProcessResult.s();
    }

    @Override
    public ProcessResult compensate(QuarkParameterInfo parameterInfo) {
        return null;
    }


}