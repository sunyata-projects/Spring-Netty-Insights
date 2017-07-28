package org.sunyata.quark.manual;

import org.sunyata.quark.basic.AbstractFlow;
import org.sunyata.quark.basic.BusinessContext;
import org.sunyata.quark.store.QuarkComponentInstance;

/**
 * Created by leo on 17/4/27.
 */
public class ManualFlow extends AbstractFlow {

    @Override
    protected QuarkComponentInstance selectPrimaryQuarkComponent(BusinessContext businessContext) {
        QuarkComponentInstance quarkComponentInstance = super.selectPrimaryQuarkComponent(businessContext);
        if (quarkComponentInstance.getOrderby().equals(businessContext.getManualComponentIndex())) {
            return null;
        }
        return quarkComponentInstance;
    }
}
