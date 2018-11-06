package org.vertx.insight.basic;

/**
 * Created by leo on 17/4/10.
 */
public interface QuarkComponent<TParameterInfo extends QuarkParameterInfo> {
    TParameterInfo getParameterInfo(BusinessContext context) throws Exception;

    ProcessResult run(BusinessContext context) throws Exception;

    ProcessResult execute(TParameterInfo parameterInfo) throws Exception;

    ProcessResult compensate(TParameterInfo parameterInfo) throws Exception;
}
