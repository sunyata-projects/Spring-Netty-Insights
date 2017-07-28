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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.exception.CanNotExecuteException;
import org.sunyata.quark.exception.CanNotFindAnnotationException;
import org.sunyata.quark.exception.CanNotFindQuarkDescriptorException;
import org.sunyata.quark.executor.DefaultExecutor;
import org.sunyata.quark.executor.Executor;
import org.sunyata.quark.ioc.ServiceLocator;
import org.sunyata.quark.stereotype.BusinessComponent;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.synchronization.StateSynchronization;

/**
 * Created by leo on 16/12/15.
 */
public abstract class AbstractBusinessComponent<TFlow extends Flow, TExecutor extends Executor> {

    private TFlow flow;
    private TExecutor executor;
    Logger logger = LoggerFactory.getLogger(AbstractBusinessComponent.class);

    public BusinessComponentDescriptor getBusinessComponentDescriptor() throws Exception {
        if (businessComponentDescriptor == null) {
            businessComponentDescriptor = initializeDescriptor();
        }
        return businessComponentDescriptor;
    }

    public AbstractBusinessComponent setBusinessComponentDescriptor(BusinessComponentDescriptor
                                                                            businessComponentDescriptor) {
        this.businessComponentDescriptor = businessComponentDescriptor;
        return this;
    }

    BusinessComponentDescriptor businessComponentDescriptor;

    public BusinessComponentDescriptor initializeDescriptor() throws Exception {
        BusinessComponent annotation = this.getClass().getAnnotation(BusinessComponent.class);
        if (annotation != null) {
            return new BusinessComponentDescriptor()
                    .setVersion(annotation.version())
                    .setBisinFriendlyName(annotation.bisinFriendlyName())
                    .setBusinName(annotation.businName())
                    .setDescription(annotation.description())
                    .setCompensationSwitch(annotation.compensationSwitch());
        } else {
            throw new CanNotFindAnnotationException("业务组件没有定义标注");
        }
    }


    public abstract TFlow initializeFlow() throws Exception;

    public TExecutor initializeExecutor() {
        return (TExecutor) new DefaultExecutor();
    }

    public TFlow getFlow() throws Exception {
        if (this.flow == null) {
            this.flow = initializeFlow();
        }
        return flow;
    }

    public AbstractBusinessComponent setFlow(TFlow flow) {
        this.flow = flow;
        return this;
    }

    public TExecutor getExecutor() {
        if (executor == null) {
            executor = initializeExecutor();
        }
        return executor;
    }

    public AbstractBusinessComponent setExecutor(TExecutor executor) {
        this.executor = executor;
        return this;
    }


    public ProcessResult run(BusinessContext businessContext) throws Exception {
        QuarkComponentInstance quarkComponentInstance = null;
        ProcessResult result = ProcessResult.r();//未知
        Flow flow = getFlow();
        QuarkComponentDescriptor quarkComponentDescriptor = null;
        try {
            quarkComponentInstance = flow.selectQuarkComponentInstance(businessContext);
            if (quarkComponentInstance == null) {
                if (businessContext.getBusinessMode() == BusinessModeTypeEnum.Normal) {
                    String msg = "此业务正向模式不能继续,SerialNO:" + businessContext.getSerialNo();
                    throw new CanNotExecuteException(msg);
                } else {
                    String msg = "此业务补偿模式不能继续,SerialNO:" + businessContext.getSerialNo();
                    logger.error(msg);
                    throw new CanNotExecuteException(msg);
                }
            }

            quarkComponentDescriptor = flow
                    .getQuarkComponentDescriptor(quarkComponentInstance.getQuarkName(),
                            quarkComponentInstance.getOrderby(), quarkComponentInstance.getSubOrder());
            if (quarkComponentDescriptor == null) {
                String msg = "can not found quarkComponentDescriptor" + quarkComponentDescriptor.getQuarkName();
                logger.error(msg);
                throw new CanNotFindQuarkDescriptorException(msg);
            }
            AbstractQuarkComponent quarkComponent = ServiceLocator.getLocator().getService
                    (quarkComponentDescriptor.getClazz());
            logger.debug("获取quark组件实例成功,Clazz:" + quarkComponentDescriptor.getClazz().getName());
            businessContext.setCurrentQuarkDescriptor(quarkComponentDescriptor);
            businessContext.setCurrentQuarkSerialNo(quarkComponentInstance.getSerialNo());
            logger.info("quark开始执行,Name:" + quarkComponentDescriptor.getTargetQuarkName());
            long begin = System.currentTimeMillis();
            result = quarkComponent.run(businessContext);
            long end = System.currentTimeMillis();
            result.setTotalMillis(end - begin);
            logger.info("quark执行完毕,Name:" + quarkComponentDescriptor.getTargetQuarkName());
        }catch (CanNotExecuteException ex){
            throw ex;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            logger.error(stackTrace);
            result.setMessage(stackTrace);
        } finally {
            result.setQuarkComponentInstance(quarkComponentInstance);
            result.setQuarkComponentDescriptor(quarkComponentDescriptor);
        }
        return result;
    }


    public void stateSync(BusinessContext businessContext, ProcessResult result) throws Exception {
        StateSynchronization synchronization = StateSynchronization.getSynchronizer(businessContext
                .getBusinessComponent().getClass());
        synchronization.stateSync(this, businessContext, result);
    }
}
