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

package org.sunyata.quark.console.components;

import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkParameterInfo;
import org.sunyata.quark.stereotype.QuarkComponent;

/**
 * Created by leo on 16/12/15.
 */
@QuarkComponent(quarkName = "SuccessQuarkComponent", quarkFriendlyName = "SuccessQuarkComponent", version = "1.0")
public class SuccessQuarkComponent extends AbstractQuarkComponent {

    public SuccessQuarkComponent() {
    }

    @Override
    public ProcessResult execute(QuarkParameterInfo parameterInfo) {
        System.out.println("hello world" + Thread.currentThread().getName());
        return ProcessResult.s();
    }

    @Override
    public ProcessResult compensate(QuarkParameterInfo parameterInfo) {
        return null;
    }

    @Override
    public QuarkParameterInfo getParameterInfo(BusinessContext context) {
        return new SuccessQuarkParameterInfo().setBusinessContext(context);
    }
}