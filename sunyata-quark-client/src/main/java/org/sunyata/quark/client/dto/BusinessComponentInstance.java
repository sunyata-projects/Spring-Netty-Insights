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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 16/12/11.
 */
public class BusinessComponentInstance implements Serializable {
    public BusinessComponentInstance() {
        items = new ArrayList<>();
        needToRetry = false;
    }

    public Timestamp getUpdateDateTime() {
        return updateDateTime;
    }

    public BusinessComponentInstance setUpdateDateTime(Timestamp updateDateTime) {
        this.updateDateTime = updateDateTime;
        return this;
    }

    public List<QuarkComponentInstance> getItems() {
        return items;
    }

    public BusinessComponentInstance setItems(List<QuarkComponentInstance> items) {
        this.items = items;
        return this;
    }

    public BusinessStatusTypeEnum getBusinStatus() {
        return businStatus;
    }

    public BusinessComponentInstance setBusinStatus(BusinessStatusTypeEnum businStatus) {
        this.businStatus = businStatus;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public BusinessComponentInstance setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public BusinessComponentInstance setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public BusinessComponentInstance setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getParameterString() {
        return parameterString;
    }

    public BusinessComponentInstance setParameterString(String parameterString) {
        this.parameterString = parameterString;
        return this;
    }

    public String getBusinCode() {
        return businCode;
    }

    public BusinessComponentInstance setBusinCode(String businCode) {
        this.businCode = businCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BusinessComponentInstance setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public BusinessComponentInstance setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getBusinName() {
        return businName;
    }

    public BusinessComponentInstance setBusinName(String businName) {
        this.businName = businName;
        return this;
    }

    public CanContinueTypeEnum getCanContinue() {
        return canContinue;
    }

    public BusinessComponentInstance setCanContinue(CanContinueTypeEnum canContinue) {
        this.canContinue = canContinue;
        return this;
    }


    public BusinessModeTypeEnum getBusinessMode() {
        return businessMode;
    }

    public BusinessComponentInstance setBusinessMode(BusinessModeTypeEnum businessMode) {
        this.businessMode = businessMode;
        return this;
    }

    public boolean isNeedToRetry() {
        return needToRetry;
    }

    public BusinessComponentInstance setNeedToRetry(boolean needToRetry) {
        this.needToRetry = needToRetry;
        return this;
    }


    /**
     * 业务流水号,全局唯一
     */
    private String serialNo;
    /**
     * 业务名称
     */
    private String businName;

    /**
     * 业务编码,相同的编码代表同一个业务
     */
    private String businCode;

    /**
     * 业务描述
     */
    private String description;
    /**
     * 业务创建时间
     */
    private Timestamp createDateTime;


    /**
     * 业务组件版本
     */
    private String version;

    /**
     * 业务执行参数,一般为json格式,可以是任意能被业务组件识别的格式
     */
    private String parameterString;

    /**
     * 元子组件执行序列,创建业务组件实例时同时创建业务元实例
     */
    private List<QuarkComponentInstance> items;

    /**
     * 业务状态
     * <p>
     * 初始化:创建完成并且没有开始执行,未完结
     * 进行中:整个流程还有未执行元子组件,或部分可失败节点未达到最大执行次数,未完结
     * 部分成功:整个流程执行完毕,但有部分可失败的节点执行失败并且达到最大执行次数,其它必须成功的节点则执行成功,已完结
     * 失败:必须成功的节点达到最大执行次数并且没有成功,或部分元子组件没有达到最大执行次数但已不可继续(如y订单重复) 已完结
     * 全部成功:已完结
     */
    private BusinessStatusTypeEnum businStatus;
    /**
     * 业务说明信息
     */
    private String notes;


    /**
     * 是否终结
     */
    private CanContinueTypeEnum canContinue;//是否终结

    /**
     * 业务最后执行时间,可能被人工,上游业务,定时器触发执行
     */
    private Timestamp updateDateTime;

    /**
     * 业务模式,是正常还是补偿
     */
    private BusinessModeTypeEnum businessMode;


    /**
     * 是否需要重试
     */
    private boolean needToRetry;

}
