package official.sketchBook.engine.components_related.integration_interfaces;

import official.sketchBook.engine.util_related.utils.RayCastHelper;

public interface RayCasterII {
    RayCastHelper getRayCastHelper();
    void updateRayCast();
}
