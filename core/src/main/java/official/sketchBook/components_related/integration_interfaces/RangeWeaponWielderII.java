package official.sketchBook.components_related.integration_interfaces;

import official.sketchBook.room_related.model.PlayableRoom;
import official.sketchBook.util_related.enumerators.types.FactionTypes;

public interface RangeWeaponWielderII {
    float getX();
    float getY();

    FactionTypes getFaction();

    PlayableRoom getOwnerRoom();
}
