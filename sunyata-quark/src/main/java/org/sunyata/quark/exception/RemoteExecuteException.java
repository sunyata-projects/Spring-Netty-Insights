package org.sunyata.quark.exception;

import org.sunyata.quark.ioc.NestedRuntimeException;

/**
 * Created by leo on 17/5/12.
 */
public class RemoteExecuteException extends NestedRuntimeException {
    public RemoteExecuteException(String msg) {
        super(msg);
    }
}
