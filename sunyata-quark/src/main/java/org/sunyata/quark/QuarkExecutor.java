package org.sunyata.quark;

import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.store.BusinessComponentInstance;

/**
 * Created by leo on 17/8/2.
 */
public interface QuarkExecutor {
    BusinessComponentInstance create(String serialNo, String businName, String sponsor, String relationId, String
            parameterString) throws
            Exception;

    BusinessComponentInstance create(String serialNo, String businName, String sponsor, String relationId, String
            parameterString, boolean
            autoRun)
            throws
            Exception;

    void run(String serialNo) throws Exception;

    ProcessResult runByManual(String serialNo, int quarkIndex, String parameters) throws Exception;

    void retry(String serialNo) throws Exception;

    void reBeginByServerId(String serverId)throws Exception;

    void retryByServerId(String serverId) throws Exception;
    void quarkNotify(String serialNo, Integer quarkIndex, ProcessResult result) throws Exception;

}
