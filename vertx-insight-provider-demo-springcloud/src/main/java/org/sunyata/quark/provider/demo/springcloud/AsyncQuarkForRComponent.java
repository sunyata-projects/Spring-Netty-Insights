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

package org.sunyata.quark.provider.demo.springcloud;

import org.springframework.stereotype.Component;
import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.basic.ProcessResult;
import org.sunyata.quark.basic.QuarkParameterInfo;
import org.sunyata.quark.json.JsonObject;
import org.sunyata.quark.stereotype.QuarkComponent;

import java.util.Random;

/**
 * Created by leo on 16/12/15.
 */
@Component("AsyncQuarkForRComponent")
@QuarkComponent(quarkName = "AsyncQuarkForRComponent", quarkFriendlyName = "AsyncQuarkForRComponent", version = "1.0")
public class AsyncQuarkForRComponent extends AbstractQuarkComponent<AsyncQuarkForRComponent.AsyncQuarkParameterInfo> {
    class AsyncQuarkParameterInfo extends QuarkParameterInfo {
        public String getField1() {
            return field1;
        }

        public AsyncQuarkParameterInfo setField1(String field1) {
            this.field1 = field1;
            return this;
        }

        @Override
        public QuarkParameterInfo parse(BusinessContext context) throws Exception {
//            if (context.getBusinessComponent().getBusinessComponentDescriptor().getBusinName() == "") {
            String parameterString = context.getInstance().getQuarkParameter().getParameter();
            JsonObject jsonObject = new JsonObject(parameterString);
            String field1 = jsonObject.getString("field1");
            return new AsyncQuarkParameterInfo().setField1(field1);
//            }
//            return null;
        }

        private String field1;
    }


    @Override
    public AsyncQuarkParameterInfo getParameterInfo(BusinessContext context) throws Exception {
        return (AsyncQuarkParameterInfo) new AsyncQuarkParameterInfo().parse(context);
    }

    @Override
    public ProcessResult execute(AsyncQuarkParameterInfo parameterInfo) throws InterruptedException {
        System.out.println(this.getClass().getName() + "-" + parameterInfo.getField1() + "-" + Thread.currentThread()
                .getName());
        Thread.sleep(10000);
        Random r = new Random();
        int i = r.nextInt(5);
        if(i==2) {
            return ProcessResult.s().setOutputParameter("money", 3000);
        }else
        {
            return ProcessResult.r().setOutputParameter("money", 3000);
        }
    }

    @Override
    public ProcessResult compensate(AsyncQuarkParameterInfo parameterInfo) {
        return null;
    }

}