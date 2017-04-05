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
import org.sunyata.quark.DefaultOrchestration;
import org.sunyata.quark.basic.AbstractBusinessComponent;
import org.sunyata.quark.basic.Orchestration;
import org.sunyata.quark.basic.QuarkComponentOptions;
import org.sunyata.quark.descriptor.QuarkComponentDescriptorFactory;
import org.sunyata.quark.embed.demo.springcloud.FooFlow;
import org.sunyata.quark.executor.DefaultExecutor;
import org.sunyata.quark.stereotype.BusinessComponent;

/**
 * Created by leo on 16/12/15.
 */
@Component
@BusinessComponent(businCode = "TwoBusinessComponent", bisinName = "业务名称", version = "1.0", description =
        "desc",
        compensationSwitch = false)
public class TwoBusinessComponent extends AbstractBusinessComponent<FooFlow, DefaultExecutor> {


    @Override
    public DefaultExecutor initializeExecutor() {
        return new DefaultExecutor();
    }

    @Override
    public FooFlow initializeFlow() throws Exception {
        Orchestration<FooFlow> orchestration =
                new DefaultOrchestration<FooFlow>()
                        .beginWith(QuarkComponentDescriptorFactory.getDescriptor(RetryQuarkComponent.class)
                                .setOptions(new QuarkComponentOptions()
                                        .setRetryLimitTimes(3)
                                        .setCanCancel(true)))
                        .succeed()

                        .next(QuarkComponentDescriptorFactory.getDescriptor(ErrorQuarkComponent.class))

                        .setBusinessComponentDescriptor(this.initializeDescriptor());

        return orchestration.orchestrate(FooFlow.class);
    }
}


