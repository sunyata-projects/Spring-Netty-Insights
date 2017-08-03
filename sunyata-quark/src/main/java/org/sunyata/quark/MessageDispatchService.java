package org.sunyata.quark;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leo on 17/7/28.
 */
public class MessageDispatchService {

    Logger logger = LoggerFactory.getLogger(MessageDispatchService.class);
    private QuarkExecutor businessManager;
    private MessageQueueService queueService;
    private QuarkCommandConfig config;

    public MessageDispatchService(QuarkExecutor businessManager, MessageQueueService queueService,
                                  QuarkCommandConfig config) {
        this.businessManager = businessManager;
        this.queueService = queueService;
        this.config = config;
    }

    AtomicInteger atomicInteger = new AtomicInteger();

    public void doDispatch() {
        Thread thread = new Thread(() -> {
            while (true) {
                DispatchItem element = null;
                try {
                    element = queueService.take();
                    int i = atomicInteger.incrementAndGet();
                    logger.info("current dequeue:{},serialNO:{}", i, element.getSerialNo());
                    element.getCommand(businessManager, queueService, config).queue();
//                    if (!element.isPrimary()) {
//                        new RetryCommand(businessManager, element.getSerialNo()).queue();
//                        //businessManager.retry(element.getSerialNo());
//                    } else {
//                        new PublicRunCommand(businessManager, element.getSerialNo()).queue();
//                        //businessManager.run(element.getSerialNo());
//                    }
                } catch (InterruptedException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                //System.out.println(System.currentTimeMillis() + "---" + element);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
