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

package org.sunyata.quark.stereotype;

import org.sunyata.quark.synchronization.DefaultCompensationStateSynchronizer;
import org.sunyata.quark.synchronization.CompensationStateSynchronizer;
import org.sunyata.quark.synchronization.DefaultStateSynchronization;
import org.sunyata.quark.synchronization.StateSynchronization;

import java.lang.annotation.*;

/**
 * Created by leo on 17/3/22.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessComponent {
    String value() default "";

    String description() default "";

    String version() default "";

    String businCode() default "";//业务标识符ccop.withdraw

    String bisinName() default "";//业务名称

    boolean compensationSwitch() default false;//业务补偿开关

    Class<? extends StateSynchronization> synchronizer() default DefaultStateSynchronization.class;

    Class<? extends CompensationStateSynchronizer> compensationSynchronizer() default
            DefaultCompensationStateSynchronizer.class;
}
