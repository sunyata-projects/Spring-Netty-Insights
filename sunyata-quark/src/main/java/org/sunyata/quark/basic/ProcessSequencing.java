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

package org.sunyata.quark.basic;

import org.sunyata.quark.descriptor.MutltipleQuarkComponentDescriptor;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by leo on 16/12/14.
 */
public class ProcessSequencing extends TreeSet<MutltipleQuarkComponentDescriptor> {

    public MutltipleQuarkComponentDescriptor getLastOne() {
        if (this.size() > 0) {
            return this.stream().skip(this.size() - 1).findFirst().get();
        }
        return null;
    }
    public QuarkComponentDescriptor getLastQuarkComponentDescriptor() {
        List<QuarkComponentDescriptor> sortedDescriptors = getSortedDescriptors();
        if (sortedDescriptors.size() > 0) {
            return sortedDescriptors.stream().skip(sortedDescriptors.size() - 1).findFirst().get();
        }
        return null;
    }
    public List<QuarkComponentDescriptor> getSortedDescriptors() {
        List<QuarkComponentDescriptor> items = new ArrayList<>();
        for (MutltipleQuarkComponentDescriptor next : this) {
            for (QuarkComponentDescriptor quarkComponentDescriptor : next.getItems()) {
                items.add(quarkComponentDescriptor);
            }
        }
        List<QuarkComponentDescriptor> collect = items.stream().sorted((o1, o2) -> {
            if (Objects.equals(o1.getOrder(), o2.getOrder())) {
                return o1.getSubOrder() - o2.getSubOrder();
            } else {
                return o1.getOrder() - o2.getOrder();
            }
        }).collect(Collectors.toList());
        return collect;
    }
}
