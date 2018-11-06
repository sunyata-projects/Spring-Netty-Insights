package org.vertx.insight.exception;

import org.sunyata.quark.ioc.NestedRuntimeException;

/**
 * Created by leo on 17/5/12.
 */
public class CanNotFindComponentInstanceException extends NestedRuntimeException {
    public CanNotFindComponentInstanceException(String msg) {
        super(msg);
    }
}
