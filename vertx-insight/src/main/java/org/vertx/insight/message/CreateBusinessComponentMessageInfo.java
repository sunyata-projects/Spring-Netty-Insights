package org.vertx.insight.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by leo on 17/5/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBusinessComponentMessageInfo implements Serializable {
    public String getSerialNo() {
        return serialNo;
    }

    public CreateBusinessComponentMessageInfo setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getBusinName() {
        return businName;
    }

    public CreateBusinessComponentMessageInfo setBusinName(String businName) {
        this.businName = businName;
        return this;
    }

    public String getParameterString() {
        return parameterString;
    }

    public CreateBusinessComponentMessageInfo setParameterString(String parameterString) {
        this.parameterString = parameterString;
        return this;
    }

    public String getSponsor() {
        return sponsor;
    }

    public CreateBusinessComponentMessageInfo setSponsor(String sponsor) {
        this.sponsor = sponsor;
        return this;
    }

    public String getRelationId() {
        return relationId;
    }

    public CreateBusinessComponentMessageInfo setRelationId(String relationId) {
        this.relationId = relationId;
        return this;
    }

    public CreateBusinessComponentMessageInfo setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
        return this;
    }

    public boolean isAutoRun() {
        return autoRun;
    }

    private String serialNo;
    private String businName;
    private String parameterString;
    private boolean autoRun;


    private String sponsor;
    private String relationId;


}
