package org.sunyata.quark.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.executor.DefaultExecutor;

import java.io.IOException;

/**
 * Created by leo on 17/5/9.
 */
public class FastExecutor extends DefaultExecutor {
    Logger logger = LoggerFactory.getLogger(FastExecutor.class);

    @Override
    protected void writeLog(BusinessContext businessContext, ProcessResult result) throws InstantiationException, IllegalAccessException, IOException {

    }
}
