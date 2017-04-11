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

package org.sunyata.quark.client.dto;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by leo on 16/12/11.
 */
public class QuarkComponentInstance implements Serializable {


    public String getSerialNo() {
        return serialNo;
    }

    public QuarkComponentInstance setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getBusinSerialNo() {
        return businSerialNo;
    }

    public QuarkComponentInstance setBusinSerialNo(String businSerialNo) {
        this.businSerialNo = businSerialNo;
        return this;
    }

    public String getQuarkName() {
        return quarkName;
    }

    public QuarkComponentInstance setQuarkName(String quarkName) {
        this.quarkName = quarkName;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public QuarkComponentInstance setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getQuarkFriendlyName() {
        return quarkFriendlyName;
    }

    public QuarkComponentInstance setQuarkFriendlyName(String quarkFriendlyName) {
        this.quarkFriendlyName = quarkFriendlyName;
        return this;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public QuarkComponentInstance setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public Integer getOrderby() {
        return orderby;
    }

    public QuarkComponentInstance setOrderby(Integer orderby) {
        this.orderby = orderby;
        return this;
    }

    public Integer getSubOrder() {
        return subOrder;
    }

    public QuarkComponentInstance setSubOrder(Integer subOrder) {
        this.subOrder = subOrder;
        return this;
    }

    public ProcessResultTypeEnum getProcessResult() {
        return processResult;
    }

    public QuarkComponentInstance setProcessResult(ProcessResultTypeEnum processResult) {
        this.processResult = processResult;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public QuarkComponentInstance setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public Integer getExecuteTimes() {
        return executeTimes;
    }

    public QuarkComponentInstance setExecuteTimes(Integer executeTimes) {
        this.executeTimes = executeTimes;
        return this;
    }

//    public CanContinueTypeEnum getCanContinue() {
//        return canContinue;
//    }
//
//    public QuarkComponentInstance setCanContinue(CanContinueTypeEnum canContinue) {
//        this.canContinue = canContinue;
//        return this;
//    }

    public ProcessResultTypeEnum getCompensationProcessResult() {
        return compensationProcessResult;
    }

    public QuarkComponentInstance setCompensationProcessResult(ProcessResultTypeEnum compensationProcessResult) {
        this.compensationProcessResult = compensationProcessResult;
        return this;
    }


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
     * 业务元执行顺序
     */
    private Integer orderby;
    /**
     * 子顺序
     */
    private Integer subOrder;

    /**
     * 继续类型
     */
    private ContinueTypeEnum continueType;
    /**
     * 执行结果
     */
    private ProcessResultTypeEnum processResult;

    /**
     * 补偿处理结果
     */
    private ProcessResultTypeEnum compensationProcessResult;
    /**
     * 备注
     */
    private String notes;
    /**
     * 已经执行次数
     */
    private Integer executeTimes;
//    /**
//     * 是否能继续
//     */
//    private CanContinueTypeEnum canContinue;



    public ContinueTypeEnum getContinueType() {
        return continueType;
    }

    public QuarkComponentInstance setContinueType(ContinueTypeEnum continueType) {
        this.continueType = continueType;
        return this;
    }


}
