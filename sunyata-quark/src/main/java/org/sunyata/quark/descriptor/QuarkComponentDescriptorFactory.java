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

package org.sunyata.quark.descriptor;

import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.exception.CanNotFindAnnotationException;
import org.sunyata.quark.stereotype.QuarkComponent;

/**
 * Created by leo on 17/3/16.
 */
public class QuarkComponentDescriptorFactory {
    public static <T extends AbstractQuarkComponent> QuarkComponentDescriptor getDescriptor(Class<T> clazz) throws
            Exception {
        QuarkComponent annotation = clazz.getAnnotation(QuarkComponent.class);
        if (annotation != null) {
            return new QuarkComponentDescriptor()
                    .setClazz(clazz)
                    .setVersion(annotation.version())
                    .setQuarkName(annotation.quarkName())
                    .setQuarkFriendlyName(annotation.quarkFriendlyName())
                    .setTargetQuarkName(annotation.quarkName());
            //T service = ServiceLocator.getLocator().getService(clazz);
        } else {
            throw new CanNotFindAnnotationException("业务组件没有定义标注");
        }
    }
}
