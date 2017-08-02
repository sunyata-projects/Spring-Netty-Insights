package org.sunyata.quark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.ProcessResult;

import java.util.concurrent.DelayQueue;

/**
 * Created by leo on 17/7/28.
 */
public class MessageQueueService {

    Logger logger = LoggerFactory.getLogger(MessageQueueService.class);

    DelayQueue<DispatchItem> queue;

    public MessageQueueService() {
        queue = new DelayQueue<>();
    }

    public DispatchItem take() throws InterruptedException {
        return queue.take();
    }

//    public void enQueue(String buString serialNo, boolean isPrimary) {
//        queue.offer(new DispatchItem(0, serialNo, isPrimary, this));
//    }

    public void enQueue(String businName,String quarkName, long delay, String serialNo, boolean isPrimary) {
        queue.offer(new DispatchItem(businName,quarkName, delay, serialNo, isPrimary));
    }

    public void enQueue(String businName,String quarkName,long delay, String serialNo, Integer quarkIndex,
                        ProcessResult result) {
        queue.offer(new DispatchItem(businName,quarkName, delay, serialNo,quarkIndex,result));
    }
//    public void enQueue(String businName,String quarkName, long delay, String serialNo, Integer quarkIndex,
//                        ProcessResult
//            result) {
//        queue.offer(new DispatchItem(businName,quarkName, delay, serialNo,quarkIndex,result));
//    }
}
