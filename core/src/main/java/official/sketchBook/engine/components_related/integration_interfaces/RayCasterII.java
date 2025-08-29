package official.sketchBook.engine.components_related.integration_interfaces;

import official.sketchBook.engine.util_related.utils.RayCastUtils;

public interface RayCasterII {
    RayCastUtils getRayCastHelper();
    void updateRayCast();
}
