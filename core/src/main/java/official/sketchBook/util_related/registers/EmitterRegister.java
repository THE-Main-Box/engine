package official.sketchBook.util_related.registers;

import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.room_related.model.PlayableRoom;

import java.util.HashMap;
import java.util.Map;

public class EmitterRegister {
    private static final Map<PlayableRoom, Map<Entity, Emitter>> emitterMap = new HashMap<>();

    public static void register(Entity entity, Emitter emitter) {
        emitterMap.computeIfAbsent(entity.getOwnerRoom(), r -> new HashMap<>()).put(entity, emitter);
    }

    public static Emitter getEmitter(Entity entity) {
        if (entity.getOwnerRoom() == null) return null;

        Map<Entity, Emitter> roomEmitters = emitterMap.get(entity.getOwnerRoom());
        if (roomEmitters == null) return null;
        return roomEmitters.get(entity);
    }

    public static void unregister(Entity entity) {
        Map<Entity, Emitter> roomEmitters = emitterMap.get(entity.getOwnerRoom());
        if (roomEmitters != null) {
            roomEmitters.remove(entity);
            if (roomEmitters.isEmpty()) {
                emitterMap.remove(entity.getOwnerRoom());
            }
        }
    }

    public static void unregisterRoom(PlayableRoom room) {
        emitterMap.remove(room);
    }
}

