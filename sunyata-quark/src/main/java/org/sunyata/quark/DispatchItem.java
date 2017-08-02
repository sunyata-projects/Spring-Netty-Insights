package org.sunyata.quark;

import org.sunyata.quark.basic.ProcessResult;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by leo on 17/7/28.
 */
public class DispatchItem implements Delayed {
    private String businName;
    private String quarkName;
    private final long delay; //延迟时间
    private String serialNo;
    private Integer quarkIndex;
    private ProcessResult result;

    QuarkCommand command;

    public QuarkCommand getCommand(QuarkExecutor businessManager, MessageQueueService messageQueueService,
                                   QuarkCommandConfig config) {
        if (result != null) {
            command = new NotifyRunCommand(businName, quarkName, serialNo, quarkIndex, result,config);
        } else {
            if (!isPrimary) {
                command = new RetryCommand(businName, quarkName, serialNo,config);
            } else {
                command = new PublicRunCommand(businName, quarkName, serialNo,config);
            }
        }
        return command.setBusinessManager(businessManager).setMessageQueueService(messageQueueService);
    }


    public String getSerialNo() {
        return serialNo;
    }

    private boolean isPrimary;
//    private MessageQueueService messageQueueService;


//    public DispatchItem setBusinessManager(BusinessManager businessManager) {
//        if (result != null) {
//            command = new NotifyRunCommand(businName, quarkName, serialNo, quarkIndex, result);
//        } else {
//            if (!isPrimary) {
//                command = new RetryCommand(businName, quarkName, serialNo);
//            } else {
//                command = new PublicRunCommand(businName, quarkName, serialNo);
//            }
//        }
//        return this;
//    }


    private final long expire;  //到期时间
    private final long now; //创建时间

    public DispatchItem(String businName, String quarkName, long delay, String serialNo, boolean isPrimary) {
        this.businName = businName;
        this.quarkName = quarkName;
        this.delay = delay;
        this.serialNo = serialNo;
        this.isPrimary = isPrimary;
        expire = System.currentTimeMillis() + delay;    //到期时间 = 当前时间+延迟时间
        now = System.currentTimeMillis();

    }


    public DispatchItem(String businName, String quarkName, long delay, String serialNo, Integer quarkIndex,
                        ProcessResult
                                result) {
        this.businName = businName;
        this.quarkName = quarkName;
        this.delay = delay;
        this.serialNo = serialNo;
        this.quarkIndex = quarkIndex;
        this.result = result;
        expire = System.currentTimeMillis() + delay;    //到期时间 = 当前时间+延迟时间
        now = System.currentTimeMillis();

    }

    /**
     * 需要实现的接口，获得延迟时间   用过期时间-当前时间
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 用于延迟队列内部比较排序   当前时间的延迟时间 - 比较对象的延迟时间
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DelayedElement{");
        sb.append("delay=").append(delay);
        sb.append(", expire=").append(expire);
        sb.append(", serialNo='").append(serialNo).append('\'');
        sb.append(", now=").append(now);
        sb.append('}');
        return sb.toString();
    }
}
