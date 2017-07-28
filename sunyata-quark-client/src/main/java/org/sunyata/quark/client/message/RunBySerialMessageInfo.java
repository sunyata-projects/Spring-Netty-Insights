package org.sunyata.quark.client.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by leo on 17/5/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunBySerialMessageInfo implements Serializable {
    public String getSerialNo() {
        return serialNo;
    }

    public RunBySerialMessageInfo setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    private String serialNo;

}
