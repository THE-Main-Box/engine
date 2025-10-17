package official.sketchBook.engine.util_related.utils.registers;

import official.sketchBook.engine.projectileRelated.util.GlobalProjectilePool;
import official.sketchBook.engine.room_related.model.PlayableRoom;
import official.sketchBook.engine.util_related.pools.PolishDamageDataPool;

import java.util.HashMap;
import java.util.Map;

public class PolishDamageDataPoolRegister {
    private static final Map<PlayableRoom, PolishDamageDataPool> poolMap = new HashMap<>();

    public static void register(PlayableRoom room, PolishDamageDataPool pool) {
        poolMap.put(room, pool);
    }

    public static PolishDamageDataPool getPool(PlayableRoom room) {
        return poolMap.get(room);
    }

    public static void unregister(PlayableRoom room) {
        poolMap.remove(room);
    }
}
