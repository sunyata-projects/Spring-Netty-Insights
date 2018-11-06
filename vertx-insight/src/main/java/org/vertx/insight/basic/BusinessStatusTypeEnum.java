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

package org.vertx.insight.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leo on 16/11/7.
 */
public enum BusinessStatusTypeEnum {
    /**
     * Initialize初始化:创建完成并且没有开始执行,未完结
     * InProgress进行中:整个流程还有未执行元子组件,或部分可失败节点未达到最大执行次数,未完结
     * PartialSuccess部分成功:整个流程执行完毕,但有部分可失败的节点执行失败并且达到最大执行次数,其它必须成功的节点则执行成功,已完结
     * Error失败:必须成功的节点达到最大执行次数并且没有成功,已完结
     * Success全部成功:已完结
     */
    Initialize("0"),


    InProgress("11"),
    PartialSuccess("12"),
    Error("13"),
    Success("16"),

    CancelInProgress("21"),
    CancelPartialSuccess("22"),
    CancelError("23"),
    CancelSuccess("26");
    /**
     * 描述
     */
    private String label;

    private BusinessStatusTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        BusinessStatusTypeEnum[] val = BusinessStatusTypeEnum.values();
        for (BusinessStatusTypeEnum e : val) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("label", e.getLabel());
            map.put("name", e.name());
            list.add(map);
        }
        return list;
    }

    public static BusinessStatusTypeEnum getEnum(String name) {
        BusinessStatusTypeEnum resultEnum = null;
        BusinessStatusTypeEnum[] enumAry = BusinessStatusTypeEnum.values();
        for (int i = 0; i < enumAry.length; i++) {
            if (enumAry[i].name().equals(name)) {
                resultEnum = enumAry[i];
                break;
            }
        }
        return resultEnum;
    }

}
