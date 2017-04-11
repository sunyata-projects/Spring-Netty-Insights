package org.sunyata.quark.embed.springcloud;

import org.sunyata.quark.basic.AbstractQuarkComponent;
import org.sunyata.quark.descriptor.QuarkComponentDescriptor;
import org.sunyata.quark.stereotype.QuarkComponent;

/**
 * Created by leo on 17/4/11.
 */
public class RemoteQuarkComponentDescriptorFactory {
    public static <T extends AbstractQuarkComponent> QuarkComponentDescriptor getDescriptor(String serviceName, String
            quarkName) throws Exception {
        Class<RemoteQuarkComponent> clazz = RemoteQuarkComponent.class;
        QuarkComponent annotation = clazz.getAnnotation(QuarkComponent.class);
        if (annotation != null) {
            QuarkComponentDescriptor quarkComponentDescriptor = new QuarkComponentDescriptor()
                    .setClazz(clazz)
                    .setVersion(annotation.version())
                    .setQuarkName(annotation.quarkName())
                    .setQuarkFriendlyName(annotation.quarkFriendlyName());
            quarkComponentDescriptor.getOptions().put("name", serviceName).put("path", "/quark/run").put
                    ("quark-name", quarkName);
            return quarkComponentDescriptor;
        } else {
            throw new Exception("业务组件没有定义标注");
        }
    }
}

