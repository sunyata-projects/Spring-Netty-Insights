package org.sunyata.quark.exception;

import org.sunyata.quark.ioc.NestedRuntimeException;

/**
 * Created by leo on 17/5/12.
 */
public class CanNotFindQuarkDescriptorException extends NestedRuntimeException {
    public CanNotFindQuarkDescriptorException(String msg) {
        super(msg);
    }
}
