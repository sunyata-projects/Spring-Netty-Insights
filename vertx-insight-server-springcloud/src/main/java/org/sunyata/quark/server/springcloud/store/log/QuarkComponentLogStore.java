package org.sunyata.quark.server.springcloud.store.log;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunyata.quark.json.Json;
import org.sunyata.quark.server.springcloud.QuarkServerProperties;
import org.sunyata.quark.server.springcloud.store.BusinessMapper;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.QuarkComponentLog;
import org.sunyata.quark.store.QuarkParameter;

import java.util.*;

/**
 * Created by leo on 17/7/21.
 */
@Component
public class QuarkComponentLogStore {
    Logger logger = LoggerFactory.getLogger(QuarkComponentLogStore.class);

    @Autowired
    BusinessMapper mapper;

    @Autowired
    QuarkServerProperties quarkServerProperties;

    @Transactional(rollbackFor = Exception.class)
    public void syncBusinessStatus(BusinessComponentInstance instance, QuarkComponentLog quarkComponentLog) {
        syncBusinessStatus(instance, Collections.singletonList(quarkComponentLog));
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncBusinessStatus(BusinessComponentInstance instance, List<QuarkComponentLog> quarkComponentLogs) {
        try {
            mapper.updateBusinessComponent(instance);
            if (instance.getOutputParameters() != null && instance.getOutputParameters().size() > 0) {
                HashMap<String, Object> outputParameters = instance.getOutputParameters();
                if (outputParameters.size() > 0) {

                    QuarkParameter quarkParameter = mapper.findQuarkParameter(instance.getSerialNo(), 2);
                    if (quarkParameter == null) {
                        String encode = Json.encode(outputParameters);
                        quarkParameter = new QuarkParameter().setBusinessSerialNo(instance.getSerialNo())
                                .setParameterType(2).setParameter(encode);
                        mapper.insertByQuarkParameter(quarkParameter);
                    } else {//更新
                        if (!StringUtils.isEmpty(quarkParameter.getParameter())) {
                            Map map = Json.decodeValue(quarkParameter.getParameter(), Map.class);
                            for (Object key : map.keySet()) {
                                outputParameters.put(String.valueOf(key), map.get(key));
                            }

                        }
                        quarkParameter.setParameter(Json.encode(outputParameters));
                        mapper.updateQuarkParameter(quarkParameter);
                    }
                }
            }
            if (quarkComponentLogs != null) {
                for (QuarkComponentLog quarkComponentLog : quarkComponentLogs) {
                    QuarkComponentInstance quarkComponentInstance = instance.getItems().stream().filter(p -> p
                            .getSerialNo()
                            .equals(quarkComponentLog.getSerialNo())).findFirst
                            ().orElse(null);
                    if (quarkComponentInstance != null) {
                        mapper.updateQuarkComponent(quarkComponentInstance);
                    }
                    if (quarkServerProperties.isLogEnable()) {
                        mapper.insertByComponentLog(quarkComponentLog);
                    } else {
                        logger.info("Quark日志:业务流水号:{},{}", instance.getSerialNo(), Json.encode(quarkComponentLog));
                    }
                }
            }
        } catch (Throwable throwable) {
            logger.error(ExceptionUtils.getStackTrace(throwable));
            throw throwable;
        }
    }

    public void syncBusinessStatus(BusinessComponentInstance instance) {
        syncBusinessStatus(instance, new ArrayList<QuarkComponentLog>());
    }
}
