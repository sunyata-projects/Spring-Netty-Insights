package org.vertx.insight;

import org.slf4j.LoggerFactory;

/**
 * Created by leo on 17/7/27.
 */
public class PublicRunCommand extends QuarkCommand {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(PublicRunCommand.class);

    public PublicRunCommand(String businName, String quarkName, String serialNo,QuarkCommandConfig config) {
        super(businName, quarkName, serialNo,config);
    }

}
