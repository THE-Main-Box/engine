package official.sketchBook.projectiles_related.emitters;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.gameDataManagement_related.GameObjectManager;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.util.GlobalProjectilePool;
import official.sketchBook.util_related.poolRegisters.ProjectilePoolRegister;

public class Emitter<T extends Projectile> {
    protected GlobalProjectilePool pool;
    protected final Class<T> type;
    protected final Entity owner;

    public Emitter(Class<T> type, Entity owner) {
        this.owner = owner;
        this.type = type;
        updatePool();
    }

    public void updatePool() {
        this.pool = ProjectilePoolRegister.getPool(this.owner.getOwnerRoom());
        if (this.pool == null)
            throw new IllegalStateException("Projectile pool not registered for the room.");
    }

    /**
     * Chama para preparar um novo projétil.
     */
    @SuppressWarnings("unchecked")
    public T obtain(Entity owner, Vector2 position) {
        T p = (T) pool.returnProjectileRequested(type);
        p.init(owner);
        p.setX(position.x);//seta a posição x
        p.setY(position.y);//seta a posição y
        return p;
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
    public void fire(T p, float x, float y, float timeToReach) {
        p.getControllerComponent().launch(new Vector2(x, y), timeToReach);
    }
}
