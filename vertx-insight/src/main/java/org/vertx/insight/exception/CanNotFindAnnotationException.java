package org.vertx.insight.exception;

import org.sunyata.quark.ioc.NestedRuntimeException;

/**
 * Created by leo on 17/5/12.
 */
public class CanNotFindAnnotationException extends NestedRuntimeException {
    public CanNotFindAnnotationException(String msg) {
        super(msg);
    }
}
