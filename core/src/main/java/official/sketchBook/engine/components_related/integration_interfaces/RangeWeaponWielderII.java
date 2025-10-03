package official.sketchBook.engine.components_related.integration_interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.components_related.toUse_component.object.WeaponWieldComponent;
import official.sketchBook.engine.room_related.model.PlayableRoom;
import official.sketchBook.game.util_related.enumerators.types.FactionTypes;

public interface RangeWeaponWielderII extends DamageDealerOwnerII{
    float getX();
    float getY();

    WeaponWieldComponent getWeaponWC();

    boolean isxAxisInverted();
    boolean isyAxisInverted();

    boolean isMoving();

    boolean isIdle();

    FactionTypes getFaction();

    PlayableRoom getOwnerRoom();

    Body getBody();
}
