package official.sketchBook.engine.projectileRelated.util;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.projectileRelated.model.Projectile;

public class ProjectileFactory {

    public static <T extends Projectile> T createByType(Class<T> type, World world){
        try {
            return type.getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao instanciar projétil do tipo: " + type.getSimpleName(), e);
        }
    }

}
