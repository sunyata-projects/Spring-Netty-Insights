package org.vertx.insight.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by leo on 17/5/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunByManualMessageInfo implements Serializable {
    public String getSerialNo() {
        return serialNo;
    }

    public RunByManualMessageInfo setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    private String serialNo;

    public Integer getQuarkIndex() {
        return quarkIndex;
    }

    public RunByManualMessageInfo setQuarkIndex(Integer quarkIndex) {
        this.quarkIndex = quarkIndex;
        return this;
    }

    public String getParameterString() {
        return parameterString;
    }

    public RunByManualMessageInfo setParameterString(String parameterString) {
        this.parameterString = parameterString;
        return this;
    }

    private Integer quarkIndex;
    private String parameterString;

}
