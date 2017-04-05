/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.embed.demo.springcloud.components;

import org.springframework.stereotype.Component;
import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkParameterInfo;
import org.sunyata.quark.json.JsonObject;
import org.sunyata.quark.stereotype.QuarkComponent;

/**
 * Created by leo on 16/12/15.
 */
@Component
@QuarkComponent(businItemCode = "ErrorQuarkComponent", businItemName = "ErrorQuarkComponent", version = "1.0")
public class ErrorQuarkComponent extends AbstractQuarkComponent<ErrorQuarkComponent.ErrorQuarkParameterInfo> {

    class ErrorQuarkParameterInfo extends QuarkParameterInfo {
        public String getField1() {
            return field1;
        }

        public ErrorQuarkParameterInfo setField1(String field1) {
            this.field1 = field1;
            return this;
        }

        @Override
        public QuarkParameterInfo parse(BusinessContext context) throws Exception {
            //if (context.getBusinessComponent().getBusinessComponentDescriptor().getBusinCode() == "") {
            String parameterString = context.getInstance().getParameterString();
            JsonObject jsonObject = new JsonObject(parameterString);
            String field1 = jsonObject.getString("field1");
            return new ErrorQuarkParameterInfo().setField1(field1);
            //}
            //return null;
        }

        private String field1;
    }


    public ErrorQuarkComponent() {
    }

    @Override
    public ErrorQuarkParameterInfo getParameterInfo(BusinessContext context) throws Exception {
        return (ErrorQuarkParameterInfo) new ErrorQuarkParameterInfo().parse(context);
    }

    @Override
    protected ProcessResult execute(ErrorQuarkParameterInfo parameterInfo) {
        System.out.println(this.getClass().getName() + "-" + parameterInfo.getField1() + "-" + Thread.currentThread()
                .getName());
        return ProcessResult.e();
    }

    @Override
    protected ProcessResult compensate(ErrorQuarkParameterInfo parameterInfo) {
        return null;
    }


}