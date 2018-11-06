package org.vertx.insight.basic;

/**
 * Created by leo on 17/5/9.
 */
public class QuarkNotifyInfo {
    public String getSerialNo() {
        return serialNo;
    }

    public QuarkNotifyInfo setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public Integer getQuarkIndex() {
        return quarkIndex;
    }

    public QuarkNotifyInfo setQuarkIndex(Integer quarkIndex) {
        this.quarkIndex = quarkIndex;
        return this;
    }

    public ProcessResult getProcessResult() {
        return processResult;
    }

    public QuarkNotifyInfo setProcessResult(ProcessResult processResult) {
        this.processResult = processResult;
        return this;
    }

    private String serialNo;
    private Integer quarkIndex;
    private ProcessResult processResult;
}
