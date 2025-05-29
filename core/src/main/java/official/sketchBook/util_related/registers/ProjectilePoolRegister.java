package official.sketchBook.util_related.registers;

import official.sketchBook.projectiles_related.util.GlobalProjectilePool;
import official.sketchBook.room_related.model.PlayableRoom;

import java.util.HashMap;
import java.util.Map;

public class ProjectilePoolRegister {

    private static final Map<PlayableRoom, GlobalProjectilePool> poolMap = new HashMap<>();

    public static void register(PlayableRoom room, GlobalProjectilePool pool) {
        poolMap.put(room, pool);
    }

    public static GlobalProjectilePool getPool(PlayableRoom room) {
        return poolMap.get(room);
    }

    public static void unregister(PlayableRoom room) {
        poolMap.remove(room);
    }
}
