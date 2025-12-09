package official.sketchBook.engine.components_related.integration_interfaces.dmg;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.toUse_component.object.DamageReceiveComponent;
import official.sketchBook.engine.room_related.model.PlayableRoom;

public interface DamageReceiverII {

    void createHutBox();

    default void onDeath(DamageDealerII dealer){
        dealer.onElimination(this);
    }

    default void onDamage(DamageDealerII dealer){
        dealer.onDamage(this);
    }

    DamageReceiveComponent getDamageReceiveC();

    Body getBody();

    float getWidth();
    float getHeight();

    short getHBMaskBit();
    short getHBCategoryBit();

    PlayableRoom getOwnerRoom();

}
