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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.descriptor.BusinessComponentDescriptor;
import org.sunyata.quark.descriptor.MutltipleQuarkComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by leo on 16/12/14.
 */
public abstract class AbstractFlow implements Flow {

    Logger logger = LoggerFactory.getLogger(AbstractFlow.class);
    private ProcessSequencing processSequencing;
    private BusinessComponentDescriptor businessComponentDescriptor;
    private Orchestration orchestration;

    public AbstractFlow() {

    }

    public void setProcessSequencing(ProcessSequencing processSequencing) {
        this.processSequencing = processSequencing;
    }

    public ProcessSequencing getProcessSequencing() {
        return processSequencing;
    }

    public BusinessComponentDescriptor getBusinessComponentDescriptor() {
        return businessComponentDescriptor;
    }

    public void setBusinessComponentDescriptor(BusinessComponentDescriptor descroptor) {
        this.businessComponentDescriptor = descroptor;
    }

    public <T extends Flow> void setOrchestration(Orchestration orchestration) {
        this.orchestration = orchestration;
    }

    public <T extends Flow> Orchestration<T> getOrchestration() {
        return orchestration;
    }

    @Override
    public MutltipleQuarkComponentDescriptor getCurrentMultipleQuarkComponentDescriptor(BusinessComponentInstance
                                                                                                instance) {
        return null;
    }

    @Override
    public QuarkComponentInstance selectQuarkComponentInstance(BusinessContext businessContext) {
        if (businessContext.getBusinessMode() == BusinessModeTypeEnum.Normal) {
            if (businessContext.isPrimary()) {
                return selectPrimaryQuarkComponent(businessContext);
            } else {
                return selectSecondaryQuarkComponent(businessContext);
            }
        } else {//补偿模式
            //// TODO: 17/3/27 lcl 实现补偿模式组件选择
        }
        return null;
//        EventBus reactor = EventBus.create();
//
//        CountDownLatch latch = new CountDownLatch(3);
//
//        reactor.on(Selectors.$("worker"), o -> {
//            System.out.println(Thread.currentThread().getName() + " worker " + o);
//            reactor.notify("orchestrator", Event.wrap(1000));
//            latch.countDown();
//            System.out.println(Thread.currentThread().getName() + " ok");
//        });
//
//        reactor.on(Selectors.$("orchestrator"), new reactor.fn.Consumer<Event<Integer>>() {
//            @Override
//            public void accept(Event<Integer> event) {
//                sendTask();
//            }
//
//            void sendTask() {
//                System.out.println(Thread.currentThread().getName() + " sendTask ");
//                reactor.notify("worker", Event.wrap(latch.getCount()));
//                latch.countDown();
//            }
//        });
//        reactor.receive(Selectors.$("workerhaha"), new Function<Event<?>, Object>() {
//            @Override
//            public Object apply(Event<?> event) {
//                return null;
//            }
//        });
//        reactor.sendAndReceive()

//        reactor.notify("orchestrator", Event.wrap(1000));
//
//        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
//
//        reactor.getProcessor().onComplete();
    }

    @Override
    public QuarkComponentDescriptor getQuarkComponentDescriptor(String code, Integer order, Integer
            subOrder) {
        Iterator<MutltipleQuarkComponentDescriptor> iterator = processSequencing.iterator();

        while (iterator.hasNext()) {
            MutltipleQuarkComponentDescriptor next = iterator.next();
            List<QuarkComponentDescriptor> items = next.getItems();
            QuarkComponentDescriptor descriptor = items.stream().filter(p ->
                    p.getQuarkName().equals(code) && Objects.equals(p.getOrder(),
                            order) && Objects.equals(p.getSubOrder(), subOrder)).findFirst().orElse(null);
            if (descriptor != null) {
                return descriptor;
            }
        }
        return null;
    }

    protected QuarkComponentInstance selectPrimaryQuarkComponent(BusinessContext businessContext) {
        List<QuarkComponentInstance> items = businessContext.getInstance().getItems();

        Stream<QuarkComponentInstance> sortedStream = items.stream().sorted((o1, o2) -> {
            if (Objects.equals(o1.getOrderby(), o2.getOrderby())) {
                return o1.getSubOrder() - o2.getSubOrder();
            } else {
                return o1.getOrderby() - o2.getOrderby();
            }
        });
        List<QuarkComponentInstance> collect = sortedStream.collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        logger.info("quark instance list of serialNO {}", businessContext.getSerialNo());
        sb.append(System.getProperty("line.separator"));
        for (QuarkComponentInstance instance : collect) {
            sb.append(instance.getTargetQuarkName() + "--" + instance.getOrderby() + "--" + instance.getSubOrder()
                    + "--" + instance.getProcessResult().getLabel());
            sb.append(System.getProperty("line.separator"));
        }
        logger.info(sb.toString());
        //第一个i
        QuarkComponentInstance instance = collect.stream().filter(p -> p.getProcessResult() == ProcessResultTypeEnum.I)
                .findFirst().orElse(null);
        if (instance == null) {
            return instance;
        }
        //所有i以前的组件实例集合
        ContinueTypeEnum continueType = instance.getContinueType();
        Stream<QuarkComponentInstance> quarkComponentInstanceStream = collect.stream().filter(p -> p.getOrderby() <
                instance.getOrderby());
        List<QuarkComponentInstance> list = null;
        if (quarkComponentInstanceStream != null) {
            list = quarkComponentInstanceStream.collect(Collectors.toList());

        }
        if (list == null) {
            return instance;
        }
        boolean b = list.stream().anyMatch(p -> (p.getProcessResult() != ProcessResultTypeEnum.S && (p
                .getContinueType() == ContinueTypeEnum.Succeed)));
        return b ? null : instance;
//        if (b) {
//            return null;
//        }
//        return instance;
    }

    protected QuarkComponentInstance selectSecondaryQuarkComponent(BusinessContext businessContext) {
        List<QuarkComponentInstance> items = businessContext.getInstance().getItems();

        Stream<QuarkComponentInstance> sortedStream = items.stream().sorted((o1, o2) -> {
            if (Objects.equals(o1.getOrderby(), o2.getOrderby())) {
                return o1.getSubOrder() - o2.getSubOrder();
            } else {
                return o1.getOrderby() - o2.getOrderby();
            }
        });

        //第一个r
        QuarkComponentInstance instance = sortedStream.filter(p -> p.getProcessResult() == ProcessResultTypeEnum.R &&
                p.getExecuteTimes() <= getQuarkComponentDescriptor(p.getQuarkName(), p.getOrderby(), p.getSubOrder()).getOptions().getRetryLimitTimes())
                .findFirst()
                .orElse(null);
        return instance;
    }


}
