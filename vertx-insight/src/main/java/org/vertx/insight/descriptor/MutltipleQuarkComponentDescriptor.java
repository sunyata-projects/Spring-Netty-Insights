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

package org.vertx.insight.descriptor;


import org.sunyata.quark.basic.Validator;
import org.sunyata.quark.exception.ValidateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 16/12/11.
 */
public class MutltipleQuarkComponentDescriptor implements Validator, Comparable<MutltipleQuarkComponentDescriptor> {

    public List<QuarkComponentDescriptor> getItems() {
        return items;
    }


    public MutltipleQuarkComponentDescriptor setItems(List<QuarkComponentDescriptor> items) {
        this.items = items;
        return this;
    }

    public MutltipleQuarkComponentDescriptor add(QuarkComponentDescriptor meta) {
        items.add(meta);
        return this;
    }

    public MutltipleQuarkComponentDescriptor setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public Integer getOrder() {
        return order;
    }

    List<QuarkComponentDescriptor> items = null;

    public MutltipleQuarkComponentDescriptor() {
        items = new ArrayList<>();
    }

    Integer order;

    @Override
    public void validate() {
        items.forEach(QuarkComponentDescriptor::validate);
        if (order < 0) {
            throw new ValidateException("order 必须大于等于0");
        }
    }


    @Override
    public int compareTo(MutltipleQuarkComponentDescriptor o) {
        return this.getOrder() - o.getOrder();
    }
}
