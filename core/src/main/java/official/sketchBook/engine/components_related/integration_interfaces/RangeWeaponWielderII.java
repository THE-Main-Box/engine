package official.sketchBook.engine.components_related.integration_interfaces;

import official.sketchBook.engine.room_related.model.PlayableRoom;
import official.sketchBook.game.util_related.enumerators.types.FactionTypes;

public interface RangeWeaponWielderII {
    float getX();
    float getY();

    FactionTypes getFaction();

    PlayableRoom getOwnerRoom();
}
