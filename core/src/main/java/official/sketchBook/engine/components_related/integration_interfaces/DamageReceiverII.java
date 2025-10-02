package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.toUse_component.object.DamageReceiveComponent;

public interface DamageReceiverII {

    void createHutBox();

    void onDeath();

    DamageReceiveComponent getDamageReceiveC();

    void onDamage();

    Body getBody();

    short getHBMaskBit();
    short getHBCategoryBit();

}
