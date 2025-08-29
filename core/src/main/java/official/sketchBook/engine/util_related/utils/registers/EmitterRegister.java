package official.sketchBook.engine.util_related.utils.registers;

import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.projectileRelated.emitters.Emitter;
import official.sketchBook.engine.room_related.model.PlayableRoom;

import java.util.HashMap;
import java.util.Map;

public class EmitterRegister {
    private static final Map<PlayableRoom, Map<RangeWeaponWielderII, Emitter>> emitterMap = new HashMap<>();

    public static void register(Emitter emitter) {
        emitterMap.computeIfAbsent(emitter.getOwner().getOwnerRoom(), r -> new HashMap<>())
            .put(emitter.getOwner(), emitter);
    }

    public static Emitter getEmitter(RangeWeaponWielderII wielder) {
        if (wielder.getOwnerRoom() == null) return null;

        Map<RangeWeaponWielderII, Emitter> roomEmitters = emitterMap.get(wielder.getOwnerRoom());
        if (roomEmitters == null) return null;
        return roomEmitters.get(wielder);
    }

    public static void unregister(RangeWeaponWielderII wielder) {
        Map<RangeWeaponWielderII, Emitter> roomEmitters = emitterMap.get(wielder.getOwnerRoom());
        if (roomEmitters != null) {
            roomEmitters.remove(wielder);
            if (roomEmitters.isEmpty()) {
                emitterMap.remove(wielder.getOwnerRoom());
            }
        }
    }

    public static void unregisterRoom(PlayableRoom room) {
        emitterMap.remove(room);
    }
}

