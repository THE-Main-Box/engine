package official.sketchBook.engine.projectileRelated.emitters;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.projectileRelated.util.GlobalProjectilePool;
import official.sketchBook.engine.projectileRelated.util.ProjectilePool;
import official.sketchBook.engine.util_related.utils.registers.ProjectilePoolRegister;

public class Emitter {
    private GlobalProjectilePool pool;
    private Class<? extends Projectile> type;
    private final RangeWeaponWielderII owner;
    private boolean configured = false;
    private ProjectilePool<?> poolOfType;

    public Emitter(RangeWeaponWielderII owner) {
        this.owner = owner;
        updatePool();
    }

    public void updatePool() {
        this.pool = ProjectilePoolRegister.getPool(this.owner.getOwnerRoom());
        if (this.pool == null)
            throw new IllegalStateException("Projectile pool not registered for the room: " + owner.getOwnerRoom());
    }

    public void configure(Class<? extends Projectile> projectileType) {
        this.type = projectileType;

        this.configured = this.type != null;
    }

    /**
     * Chama para preparar um novo projétil.
     */
    @SuppressWarnings("unchecked")
    public Projectile obtain(Vector2 originPosition) {
        if (!configured || type == null || pool == null) {
            return null;
        }

        poolOfType = pool.getPoolOf(type);
        if (poolOfType != null && !poolOfType.canSpawnNewProjectile()) {
            return null;
        }

        Projectile p = pool.returnProjectileRequested(type);
        p.init(owner);
        p.setX(originPosition.x);
        p.setY(originPosition.y);
        p.getBody().setTransform(originPosition, p.getRotation());

        return p;
    }

    public GlobalProjectilePool getPool() {
        return pool;
    }

    /**
     * Dispara o projétil que está “primed”.
     *
     * @param timeToReach Tempo estimado para chegar no alvo,
     *                    pode ser ignorado caso o projétil não seja influenciado pela gravidade
     * @param p           projétil a ser atirado (precisa ser do mesmo tipo do emissor)
     * @param x           direção horizontal que o projétil deve ser lançado,
     *                    porém, caso o projétil seja afetado pela gravidade,
     *                    ele irá ser tratado como a distância que o projétil deverá alcançar, quer seja positiva ou negativa
     * @param y           direção vertical que o projétil deve ser lançado,
     *                    porém, caso o projétil seja afetado pela gravidade,
     *                    ele irá ser tratado como a altura que o projétil deverá alcançar, quer seja positiva ou negativa
     */
    public void fire(Projectile p, float x, float y, float timeToReach) {
        if(p == null) return;
        p.getControllerComponent().launch(new Vector2(x, y), timeToReach);
    }

    public RangeWeaponWielderII getOwner() {
        return owner;
    }

    public Class<? extends Projectile> getType() {
        return type;
    }

    public boolean isConfigured() {
        return configured;
    }
}
