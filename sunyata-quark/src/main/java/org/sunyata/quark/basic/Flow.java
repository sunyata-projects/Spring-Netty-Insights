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

import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.descriptor.MutltipleQuarkComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.BusinessComponentInstance;

/**
 * Created by leo on 16/12/14.
 */
public interface Flow {

    void setProcessSequencing(ProcessSequencing processSequencing);

    ProcessSequencing getProcessSequencing();

    BusinessComponentDescriptor getBusinessComponentDescriptor();

    void setBusinessComponentDescriptor(BusinessComponentDescriptor descroptor);

    <T extends Flow> void setOrchestration(Orchestration tAbstractOrchestration);

    <T extends Flow> Orchestration<T> getOrchestration();

    MutltipleQuarkComponentDescriptor getCurrentMultipleQuarkComponentDescriptor(BusinessComponentInstance instance);

    QuarkComponentInstance selectQuarkComponentInstance(BusinessContext businessContext);

    QuarkComponentDescriptor getQuarkComponentDescriptor(String code, Integer order, Integer
            subOrder);


}
