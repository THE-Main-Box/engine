package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.toUse_component.object.DamageReceiveComponent;
import official.sketchBook.engine.room_related.model.PlayableRoom;

public interface DamageReceiverII {

    void createHutBox();

    void onDeath();

    DamageReceiveComponent getDamageReceiveC();

    void onDamage();

    Body getBody();

    float getWidth();
    float getHeight();

    short getHBMaskBit();
    short getHBCategoryBit();

    PlayableRoom getOwnerRoom();

}
