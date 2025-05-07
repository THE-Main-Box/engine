package official.sketchBook.projectiles_related.emitters;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.util.GlobalProjectilePool;

public class Emitter<T extends Projectile>{
    protected final GlobalProjectilePool pool;
    protected final Class<T> type;
    protected T current;

    private final boolean autoLaunch; // dispara assim que obtido
    private final boolean pausePhysicsUntilFire; // pausa física enquanto não lançar

    public Emitter(GlobalProjectilePool pool, Class<T> type,
                   boolean autoLaunch, boolean pausePhysicsUntilFire) {
        this.pool = pool;
        this.type = type;
        this.autoLaunch = autoLaunch;
        this.pausePhysicsUntilFire = pausePhysicsUntilFire;
    }

    /** Chama para preparar um novo projétil. */
    @SuppressWarnings("unchecked")
    public void prime(Entity owner, Vector2 position, Vector2 direction, float speed) {
        current = (T) pool.returnProjectileRequested(type);
        current.init(owner);
        current.setX(position.x);
        current.setY(position.y);
        current.setActive(true);

        if (pausePhysicsUntilFire) {
            // tranca movimento até o fire()
            current.getBody().setActive(false);
        }

        if (autoLaunch) {
            fire(direction, speed);
        }
    }

    /** Dispara o projétil que está “primed”. */
    public void fire(Vector2 direction, float speed) {
        if (current == null) return;
        // reativa o corpo e lança
        if (pausePhysicsUntilFire) {
            current.getBody().setActive(true);
        }
        current.getControllerComponent().launch(
            new Vector2(current.getX(), current.getY()),
            direction, speed
        );
        current = null; // pronto para próxima priming
    }
}
