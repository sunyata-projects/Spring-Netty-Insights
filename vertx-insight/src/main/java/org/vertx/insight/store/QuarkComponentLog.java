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

package org.vertx.insight.store;

import org.sunyata.quark.basic.ProcessResultTypeEnum;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by leo on 16/12/11.
 */
public class QuarkComponentLog implements Serializable {

    /**
     * 日志编号
     */
    private Integer logId;
    /**
     * 业务元流水号
     */
    private String serialNo;

    /**
     * 业务流水号
     */
    private String businSerialNo;

    /**
     * 业务元编码,相同的业务元组件,编码相同
     */
    private String quarkName;

    /**
     * 业务元版本
     */
    private String version;
    /**
     * 业务元名称
     */
    private String quarkFriendlyName;
    /**
     * 创建时间
     */
    private Timestamp createDateTime;
    /**
     * 执行结果
     */
    private ProcessResultTypeEnum processResult;
    /**
     * 执行备注
     */
    private String notes;
    /**
     * 执行结果输出
     */
    private String processResultString;
    private String totalMilliseconds;

    public String getBeginMilliseconds() {
        return beginMilliseconds;
    }

    public QuarkComponentLog setBeginMilliseconds(String beginMilliseconds) {
        this.beginMilliseconds = beginMilliseconds;
        return this;
    }

    private String beginMilliseconds;


    public String getBusinSerialNo() {
        return businSerialNo;
    }

    public QuarkComponentLog setBusinSerialNo(String businSerialNo) {
        this.businSerialNo = businSerialNo;
        return this;
    }


    public Integer getLogId() {
        return logId;
    }

    public QuarkComponentLog setLogId(Integer logId) {
        this.logId = logId;
        return this;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public QuarkComponentLog setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getQuarkName() {
        return quarkName;
    }

    public QuarkComponentLog setQuarkName(String quarkName) {
        this.quarkName = quarkName;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public QuarkComponentLog setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getQuarkFriendlyName() {
        return quarkFriendlyName;
    }

    public QuarkComponentLog setQuarkFriendlyName(String quarkFriendlyName) {
        this.quarkFriendlyName = quarkFriendlyName;
        return this;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public QuarkComponentLog setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public ProcessResultTypeEnum getProcessResult() {
        return processResult;
    }

    public QuarkComponentLog setProcessResult(ProcessResultTypeEnum processResult) {
        this.processResult = processResult;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public QuarkComponentLog setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getProcessResultString() {
        return processResultString;
    }

    public QuarkComponentLog setProcessResultString(String processResultString) {
        this.processResultString = processResultString;
        return this;
    }

    public QuarkComponentLog setTotalMilliseconds(String totalMilliseconds) {
        this.totalMilliseconds = totalMilliseconds;
        return this;
    }

    public String getTotalMilliseconds() {
        return totalMilliseconds;
    }
}
