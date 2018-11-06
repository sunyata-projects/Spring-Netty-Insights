package org.vertx.insight;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

/**
 * Created by leo on 17/7/27.
 */
public class RetryCommand extends QuarkCommand {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RetryCommand.class);

    public RetryCommand(String businName, String quarkName, String serialNo,QuarkCommandConfig config) {
        super(businName, quarkName, serialNo,config);
    }

    @Override
    protected Object run() throws Exception {
        try {
            //long now = System.currentTimeMillis();
            quarkExecutor.retry(this.serialNo);
            return null;
        } catch (Exception ex) {
            logger.error("ERROR:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }
}
