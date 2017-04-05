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

import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.QuarkComponentInstance;

/**
 * Created by leo on 17/3/16.
 */
public class ProcessResult {
    private ProcessResultTypeEnum processResultType;//处理结果
//    private CanContinueTypeEnum canContinueType;//是否可以继续
//    private boolean needCancel;//是否需要补偿
    private QuarkComponentInstance quarkComponentInstance;
    private QuarkComponentDescriptor quarkComponentDescriptor;

    public String getProcessResultString() {
        return processResultString;
    }

    public ProcessResult setProcessResultString(String processResultString) {
        this.processResultString = processResultString;
        return this;
    }

    private String processResultString;//接口返回数据

//    public boolean isNeedCancel() {
//        return needCancel;
//    }
//
//    public ProcessResult setNeedCancel(boolean needCancel) {
//        this.needCancel = needCancel;
//        return this;
//    }

    public ProcessResultTypeEnum getProcessResultType() {
        return processResultType;
    }

    public ProcessResult setProcessResultType(ProcessResultTypeEnum processResultType) {
        this.processResultType = processResultType;
        return this;
    }

//    public CanContinueTypeEnum getCanContinueType() {
//        return canContinueType;
//    }
//
//    public ProcessResult setCanContinueType(CanContinueTypeEnum canContinueType) {
//        this.canContinueType = canContinueType;
//        return this;
//    }

    public static ProcessResult s() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.S);
    }

    public static ProcessResult e() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.E);
    }

    public static ProcessResult r() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.R);
    }

//    public static ProcessResult w() {
//        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.W).setCanContinueType
//                (CanContinueTypeEnum.CanContinue);
//    }

    public void setQuarkComponentInstance(QuarkComponentInstance quarkComponentInstance) {
        this.quarkComponentInstance = quarkComponentInstance;
    }

    public QuarkComponentInstance getQuarkComponentInstance() {
        return quarkComponentInstance;
    }

    public void setQuarkComponentDescriptor(QuarkComponentDescriptor quarkComponentDescriptor) {
        this.quarkComponentDescriptor = quarkComponentDescriptor;
    }

    public QuarkComponentDescriptor getQuarkComponentDescriptor() {
        return quarkComponentDescriptor;
    }
}
