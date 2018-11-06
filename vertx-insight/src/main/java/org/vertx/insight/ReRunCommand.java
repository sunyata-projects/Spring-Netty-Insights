package org.vertx.insight;

import org.slf4j.LoggerFactory;

/**
 * Created by leo on 17/7/27.
 */
public class ReRunCommand extends QuarkCommand {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ReRunCommand.class);


    public ReRunCommand(String businName, String quarkName, String serialNo,QuarkCommandConfig config) {
        super(businName, quarkName, serialNo,config);

    }
}
