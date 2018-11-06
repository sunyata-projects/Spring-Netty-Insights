package org.vertx.insight;

/**
 * Created by leo on 17/8/2.
 */
public class QuarkCommandConfig {

    public int getHystrixCommandThreadPoolCoreSize() {
        return hystrixCommandThreadPoolCoreSize;
    }

    public QuarkCommandConfig setHystrixCommandThreadPoolCoreSize(int hystrixCommandThreadPoolCoreSize) {
        this.hystrixCommandThreadPoolCoreSize = hystrixCommandThreadPoolCoreSize;
        return this;
    }

    public boolean isHystrixCommandExecutionTimeoutEnable() {
        return hystrixCommandExecutionTimeoutEnable;
    }

    public QuarkCommandConfig setHystrixCommandExecutionTimeoutEnable(boolean hystrixCommandExecutionTimeoutEnable) {
        this.hystrixCommandExecutionTimeoutEnable = hystrixCommandExecutionTimeoutEnable;
        return this;
    }

    public int getHystrixCommandExecutionTimeoutInMilliseconds() {
        return hystrixCommandExecutionTimeoutInMilliseconds;
    }

    public QuarkCommandConfig setHystrixCommandExecutionTimeoutInMilliseconds(int hystrixCommandExecutionTimeoutInMilliseconds) {
        this.hystrixCommandExecutionTimeoutInMilliseconds = hystrixCommandExecutionTimeoutInMilliseconds;
        return this;
    }

    public boolean isHystrixCommandCircuitBreakerEnable() {
        return hystrixCommandCircuitBreakerEnable;
    }

    public QuarkCommandConfig setHystrixCommandCircuitBreakerEnable(boolean hystrixCommandCircuitBreakerEnable) {
        this.hystrixCommandCircuitBreakerEnable = hystrixCommandCircuitBreakerEnable;
        return this;
    }

    public int getHystrixCommandCircuitBreakerRequestVolumeThreshold() {
        return hystrixCommandCircuitBreakerRequestVolumeThreshold;
    }

    public QuarkCommandConfig setHystrixCommandCircuitBreakerRequestVolumeThreshold(int hystrixCommandCircuitBreakerRequestVolumeThreshold) {
        this.hystrixCommandCircuitBreakerRequestVolumeThreshold = hystrixCommandCircuitBreakerRequestVolumeThreshold;
        return this;
    }

    private int hystrixCommandThreadPoolCoreSize;


    private boolean hystrixCommandExecutionTimeoutEnable;


    private int hystrixCommandExecutionTimeoutInMilliseconds;



    private boolean hystrixCommandCircuitBreakerEnable;


    private int hystrixCommandCircuitBreakerRequestVolumeThreshold;



}
