package org.vertx.insight.exception;

import org.sunyata.quark.ioc.NestedRuntimeException;

/**
 * Created by leo on 17/5/12.
 */
public class CanNotExecuteException extends NestedRuntimeException {
    public CanNotExecuteException(String msg) {
        super(msg);
    }
}
