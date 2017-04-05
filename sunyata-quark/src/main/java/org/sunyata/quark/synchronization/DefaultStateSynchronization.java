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

package org.sunyata.quark.synchronization;

import org.sunyata.quark.basic.*;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.BusinessComponentInstance;

/**
 * Created by leo on 17/3/22.
 */
public class DefaultStateSynchronization implements StateSynchronization {

    public void stateSync(AbstractBusinessComponent businessComponent, BusinessContext businessContext,
                          ProcessResult
                                  processResult) throws Exception {
        if (businessContext.getBusinessMode() != BusinessModeTypeEnum.Normal) {
            throw new Exception("非正常业务模式");
        }

        if (processResult.getProcessResultType() == ProcessResultTypeEnum.S) {
            syncForSStatus(businessComponent, businessContext, processResult);
        } else if (processResult.getProcessResultType() == ProcessResultTypeEnum.R) {
            syncForRStatus(businessComponent, businessContext, processResult);
        } else if (processResult.getProcessResultType() == ProcessResultTypeEnum.E) {
            syncForEStatus(businessComponent, businessContext, processResult);
        } else {
            throw new Exception("无法识别的处理结果");
        }
    }


    /**
     * 同步成功执行后状态
     *
     * @param businessComponent
     * @param businessContext
     * @param processResult
     */
    private void syncForSStatus(AbstractBusinessComponent businessComponent, BusinessContext businessContext,
                                ProcessResult
                                        processResult) throws Exception {
        if (processResult.getProcessResultType() != ProcessResultTypeEnum.S) {
            throw new Exception("状态不正确定");
        }
        QuarkComponentInstance quarkComponentInstance = processResult.getQuarkComponentInstance();
        quarkComponentInstance.setProcessResult(ProcessResultTypeEnum.S);
//        quarkComponentInstance.setCanContinue(CanContinueTypeEnum.CanNotContinue);
        BusinessComponentInstance instance = businessContext.getInstance();
        syncBusinessStatus(businessComponent, businessContext, processResult);
    }


    /**
     * 同步未知执行后状态
     *
     * @param businessComponent
     * @param businessContext
     * @param processResult
     */
    private void syncForRStatus(AbstractBusinessComponent businessComponent, BusinessContext businessContext,
                                ProcessResult
                                        processResult) throws Exception {
        if (processResult.getProcessResultType() != ProcessResultTypeEnum.R) {
            throw new Exception("状态不正确定");
        }
        QuarkComponentInstance quarkComponentInstance = processResult.getQuarkComponentInstance();
        quarkComponentInstance.setProcessResult(ProcessResultTypeEnum.R);

        BusinessComponentInstance instance = businessContext.getInstance();
        //重试次数加1
        Integer executeTimes = quarkComponentInstance.getExecuteTimes() == null ? 0 : quarkComponentInstance
                .getExecuteTimes();
        quarkComponentInstance.setExecuteTimes(++executeTimes);

        if (executeTimes > processResult.getQuarkComponentDescriptor().getOptions().getRetryLimitTimes()) {
//            quarkComponentInstance.setCanContinue(CanContinueTypeEnum.CanNotContinue);
        } else {
            instance.setNeedToRetry(true);
        }

        syncBusinessStatus(businessComponent, businessContext, processResult);

    }

    /**
     * 同步错误执行后状态
     *
     * @param businessComponent
     * @param businessContext
     * @param processResult
     */
    private void syncForEStatus(AbstractBusinessComponent businessComponent, BusinessContext businessContext,
                                ProcessResult
                                        processResult) throws Exception {
        if (processResult.getProcessResultType() != ProcessResultTypeEnum.E) {
            throw new Exception("状态不正确定");
        }
        QuarkComponentInstance quarkComponentInstance = processResult.getQuarkComponentInstance();
        quarkComponentInstance.setProcessResult(ProcessResultTypeEnum.E);
//        quarkComponentInstance.setCanContinue(CanContinueTypeEnum.CanNotContinue);

        syncBusinessStatus(businessComponent, businessContext, processResult);

        BusinessComponentInstance instance = businessContext.getInstance();

        if (processResult.getQuarkComponentDescriptor().getContinueType() == ContinueTypeEnum.Succeed) {
            instance.setBusinStatus(BusinessStatusTypeEnum.Error);
            instance.setCanContinue(CanContinueTypeEnum.CanNotContinue);
            if (businessComponent.getBusinessComponentDescriptor().getCompensationSwitch()) {
                instance.setBusinessMode(BusinessModeTypeEnum.Compensation);//开启业务补偿模式
                instance.setCanContinue(CanContinueTypeEnum.CanContinue);//可以继续执行
                instance.setNeedToRetry(false);//不需要重试
            }
        }
    }


    /***
     * 同步业务状态
     *
     * @param businessComponent
     * @param businessContext
     * @param processResult
     */
    private void syncBusinessStatus(AbstractBusinessComponent businessComponent, BusinessContext businessContext,
                                    ProcessResult processResult) {
        /**********以下是整体业务状态逻辑**********/
        //          所有节点均成功:标记为成功
        //          必须成功节点均已成功,其它非必须成功节点状态不等于初始化 :标记为部分成功
        //          必须成功节点失败的节点数量等于或大于一个 or 必须成功的节点处理结果为未知,重试超限 :标记为失败
        //          必须成功节点未知的重试次数未超限:标记为进行中
        //          必须成功节点未知的重试次数超限:标记为失败
        BusinessComponentInstance instance = businessContext.getInstance();
        //业务是否执行完毕,不能继续
        long count = instance.getItems().stream().filter(p -> p.getProcessResult() == ProcessResultTypeEnum.I || (p
                .getProcessResult() == ProcessResultTypeEnum.R && (p.getExecuteTimes() == null ? 0 : p
                .getExecuteTimes()) < processResult
                .getQuarkComponentDescriptor().getOptions().getRetryLimitTimes())).count();
        if (count > 0) {
            instance.setCanContinue(CanContinueTypeEnum.CanContinue);
        } else {
            instance.setCanContinue(CanContinueTypeEnum.CanNotContinue);
        }
        if (instance.getCanContinue() == CanContinueTypeEnum.CanNotContinue) {//业务不能继续

            count = instance.getItems().stream().filter(p -> p.getProcessResult() == ProcessResultTypeEnum.S).count();
            if (count == instance.getItems().size()) {// 所有节点均成功:标记为成功
                instance.setBusinStatus(BusinessStatusTypeEnum.Success);
                return;
            }

            //必须执行成功节点没有成功(失败或者重试超限)数量等于或大于一个
            count = instance.getItems().stream().filter(p -> p.getProcessResult() != ProcessResultTypeEnum.S && p
                    .getContinueType() == ContinueTypeEnum.Succeed)
                    .count();

            if (count > 0) {
                instance.setBusinStatus(BusinessStatusTypeEnum.Error);
                return;
            }

            //必须成功节点均已成功,其它非必须成功节点状态不等于初始化 :标记为部分成功
            //所有必须执行成功的节点没有执行成功的数量
            count = instance.getItems().stream().filter(p -> p.getProcessResult() == ProcessResultTypeEnum.S && p
                    .getContinueType() == ContinueTypeEnum.Succeed)
                    .count();

            long count2 = instance.getItems().stream().filter(p -> p.getContinueType() == ContinueTypeEnum.Succeed)
                    .count();

            if (count == count2) {
                instance.setBusinStatus(BusinessStatusTypeEnum.PartialSuccess);
                return;
            }


        } else {
            instance.setBusinStatus(BusinessStatusTypeEnum.InProgress);
        }

    }


}

