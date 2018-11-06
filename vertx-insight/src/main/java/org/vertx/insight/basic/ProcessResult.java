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

package org.vertx.insight.basic;

import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.QuarkComponentInstance;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by leo on 17/3/16.
 */
public class ProcessResult implements Serializable {

    private ProcessResultTypeEnum processResultType;//处理结果
    private QuarkComponentInstance quarkComponentInstance;
    private QuarkComponentDescriptor quarkComponentDescriptor;
    private String message;//接口返回数据
    private Object body;//返回的消息体
    private long totalMillis;
    private long beginMillis;


    private int manualReducePriority;//手动减少优先级

    public Object getBody() {
        return body;
    }

    public ProcessResult setBody(Object body) {
        this.body = body;
        return this;
    }

    public HashMap<String, Object> getOutputParameterMaps() {
        return outputParameterMaps;
    }

    public ProcessResult setOutputParameterMaps(HashMap<String, Object> outputParameterMaps) {
        this.outputParameterMaps = outputParameterMaps;
        return this;
    }

    private HashMap<String, Object> outputParameterMaps = new HashMap<>();


    public ProcessResult setOutputParameter(String key, Object value) {
        outputParameterMaps.put(key, value);
        return this;
    }

    public Object getOutputParameter(String key, Object defaultValue) {
        return outputParameterMaps.getOrDefault(key, defaultValue);
    }


    public String getMessage() {
        return message;
    }

    public ProcessResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getManualReducePriority() {
        return manualReducePriority;
    }

    public ProcessResult setManualReducePriority(int manualReducePriority) {
        this.manualReducePriority = manualReducePriority;
        return this;
    }

    public ProcessResultTypeEnum getProcessResultType() {
        return processResultType;
    }

    public ProcessResult setProcessResultType(ProcessResultTypeEnum processResultType) {
        this.processResultType = processResultType;
        return this;
    }

    public static ProcessResult s() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.S);
    }

    public static ProcessResult e() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.E);
    }

    public static ProcessResult r() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.R);
    }

    public static ProcessResult n() {
        return new ProcessResult().setProcessResultType(ProcessResultTypeEnum.N);
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

    public void setTotalMillis(long totalMillis) {
        this.totalMillis = totalMillis;
    }

    public long getTotalMillis() {
        return totalMillis;
    }

    public void setBeginMillis(long beginMillis) {
        this.beginMillis = beginMillis;
    }

    public long getBeginMillis() {
        return beginMillis;
    }
}
