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
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.util.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;

/**
 * Created by leo on 17/3/22.
 */
public class DefaultStateSynchronization implements StateSynchronization {

    public void stateSync(AbstractBusinessComponent businessComponent, BusinessContext businessContext,
                          ProcessResult
                                  processResult) throws Exception {
        if (businessContext.getBusinessMode() != BusinessModeTypeEnum.Normal) {
            throw new Exception("同步状态时出错,非正向业务模式,SerialNo:" + businessContext.getSerialNo());
        }

        if (processResult.getProcessResultType() == ProcessResultTypeEnum.S) {
            syncForSStatus(businessComponent, businessContext, processResult);
        } else if (processResult.getProcessResultType() == ProcessResultTypeEnum.R) {
            syncForRStatus(businessComponent, businessContext, processResult);
        } else if (processResult.getProcessResultType() == ProcessResultTypeEnum.E) {
            syncForEStatus(businessComponent, businessContext, processResult);
        } else {
            throw new Exception("同步状态时出错,无法识别的处理结果,SerialNO:" + businessContext.getSerialNo());
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
            throw new Exception("同步成功执行状态时出错,状态应为成功(S)");
        }
        QuarkComponentInstance quarkComponentInstance = processResult.getQuarkComponentInstance();
        quarkComponentInstance.setProcessResult(ProcessResultTypeEnum.S);
        syncBusinessStatus(businessComponent, businessContext, processResult);
    }


    private int calculatPriority(ProcessResult result, int retryTimes, long fromDateLong) throws ParseException {
        String fromDateString = DateUtils.longToString(fromDateLong, "yyyy-MM-dd hh:mm");
        String toDateString = DateUtils.longToString(System.currentTimeMillis(), "yyyy-MM-dd hh:mm");
        int minutes = DateUtils.diffToMinutes(fromDateString, toDateString);
        int createDateTimePriority = minutes / 8;
        if (result.getProcessResultType() == ProcessResultTypeEnum.S) {
            return 0;
        } else if (result.getProcessResultType() == ProcessResultTypeEnum.R) {
            return retryTimes + 1 + (-1) * createDateTimePriority;
        } else {
            return 0;
        }
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
            throw new Exception("同步存疑执行状态时出错,状态应为存疑(R)");
        }
        QuarkComponentInstance quarkComponentInstance = processResult.getQuarkComponentInstance();
        quarkComponentInstance.setProcessResult(ProcessResultTypeEnum.R);

        BusinessComponentInstance instance = businessContext.getInstance();
        //重试次数加1
        Integer executeTimes = quarkComponentInstance.getExecuteTimes() == null ? 0 : quarkComponentInstance
                .getExecuteTimes();
        quarkComponentInstance.setExecuteTimes(++executeTimes);

        if (executeTimes >= processResult.getQuarkComponentDescriptor().getOptions().getRetryLimitTimes()) {
//            quarkComponentInstance.setCanContinue(CanContinueTypeEnum.CanNotContinue);
            instance.setNeedToRetry(false);
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
            throw new Exception("同步失败执行状态时出错,状态应为失败(E)");
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
                                    ProcessResult processResult) throws Exception {
        /**********以下是整体业务状态逻辑**********/
        //          所有节点均成功:标记为成功
        //          必须成功节点均已成功,其它非必须成功节点状态不等于初始化 :标记为部分成功
        //          必须成功节点失败的节点数量等于或大于一个 or 必须成功的节点处理结果为未知,重试超限 :标记为失败
        //          必须成功节点未知的重试次数未超限:标记为进行中
        //          必须成功节点未知的重试次数超限:标记为失败
        BusinessComponentInstance instance = businessContext.getInstance();
        boolean canContinue = true;
        instance.setCanContinue(CanContinueTypeEnum.CanContinue);
        int length = instance.getItems().size();
        for (int i = 0; i < length; i++) {
            QuarkComponentInstance quarkComponentInstance = instance.getItems().get(i);
            QuarkComponentDescriptor quarkComponentDescriptor = businessComponent.getFlow()
                    .getQuarkComponentDescriptor(quarkComponentInstance.getQuarkName(),
                            quarkComponentInstance.getOrderby(), quarkComponentInstance.getSubOrder());
            if (i != length - 1) {//不是最后一个
                if (quarkComponentInstance.getContinueType() == ContinueTypeEnum.Succeed) {
                    if (quarkComponentInstance.getProcessResult() == ProcessResultTypeEnum.I) {
                    } else if (quarkComponentInstance.getProcessResult() == ProcessResultTypeEnum.R) {
                        int executeTimes = quarkComponentInstance.getExecuteTimes() == null ? 0 :
                                quarkComponentInstance.getExecuteTimes();
                        if (executeTimes >= quarkComponentDescriptor.getOptions()
                                .getRetryLimitTimes()) {
                            canContinue = false;
                        }
                    } else if (quarkComponentInstance.getProcessResult() == ProcessResultTypeEnum.E) {
                        canContinue = false;
                    }
                }
            } else {//最后一个
                if (quarkComponentInstance.getProcessResult() == ProcessResultTypeEnum.S) {
                    canContinue = false;
                } else if (quarkComponentInstance.getProcessResult() == ProcessResultTypeEnum.R) {
                    int executeTimes = quarkComponentInstance.getExecuteTimes() == null ? 0 : quarkComponentInstance
                            .getExecuteTimes();

                    if (executeTimes >= quarkComponentDescriptor.getOptions()
                            .getRetryLimitTimes()) {
                        canContinue = false;
                    }
                } else if (quarkComponentInstance.getProcessResult() == ProcessResultTypeEnum.E) {
                    canContinue = false;
                } else {//I
                }
            }
            if (!canContinue) {
                instance.setCanContinue(CanContinueTypeEnum.CanNotContinue);
                break;
            }
        }
        long count = 0;
        if (instance.getCanContinue() == CanContinueTypeEnum.CanNotContinue) {//业务不能继续

            count = instance.getItems().stream().filter(p -> p.getProcessResult() == ProcessResultTypeEnum.S)
                    .count();
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

            if (count == count2) {//因为以上已经判断过所有节点均已经成功的情况,所以这里只可能出现部分成功
                instance.setBusinStatus(BusinessStatusTypeEnum.PartialSuccess);
                return;
            }
        } else {
            instance.setBusinStatus(BusinessStatusTypeEnum.InProgress);
        }

        instance.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
        long timeLong = instance.getCreateDateTime().getTime();

        instance.setPriority(calculatPriority(processResult, processResult.getQuarkComponentInstance()
                .getExecuteTimes(), instance.getCreateDateTime().getTime()));
//        if (processResult.getQuarkComponentDescriptor().isAsync()) {
//            if (processResult.getProcessResultType() == ProcessResultTypeEnum.R) {
//
//            }
//        }
    }


}

