package org.sunyata.quark.exception;

import org.sunyata.quark.ioc.NestedRuntimeException;

/**
 * Created by leo on 17/5/12.
 */
public class CanNotFindComponentException extends NestedRuntimeException {
    public CanNotFindComponentException(String msg) {
        super(msg);
    }
}
