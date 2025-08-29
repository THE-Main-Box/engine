package official.sketchBook.engine.components_related.integration_interfaces;

import official.sketchBook.engine.components_related.toUse_component.object.DamageReceiveComponent;

public interface DamageReceiverII {

    void createHutBox();

    void die();

    DamageReceiveComponent getDamageReceiveC();

    short getHBMaskBit();
    short getHBCategoryBit();

}
