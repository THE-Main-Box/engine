package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.gameObject_related.util.AnchorPoint;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.registers.EmitterRegister;

import java.util.Objects;

public abstract class RangeWeapon<T extends RangeWeapon<T>> extends BaseWeapon<T> {

    protected Class<? extends Projectile> projectileType;
    protected Emitter projectileEmitter;

    protected boolean recharging;

    protected TimerComponent rechargingTimeLimit;

    protected RangeWeapon(Class<T> weaponClass, Entity owner, AnchorPoint point) {
        super(weaponClass, owner, point);
        this.projectileEmitter = EmitterRegister.getEmitter(owner);
        Objects.requireNonNull(projectileEmitter, "Emitter must be registered to use this weapon");

        this.initSpriteSheet();
        this.initAnimations();
    }


    protected void configProjectileTypeOnEmitter(Class<? extends Projectile> type) {
        this.projectileType = type;
        this.projectileEmitter.configure(type);
    }

    public abstract void recharge();

    /**
     * Ponto de disparo com base na direção passada.
     * <p>
     * Passamos a posição inicial com a posição da arma
     * mais o seu offset padrão mais um offset de inicialização de projétil
     *
     * @param direction Enumerador do tipo direção, é passado como identificador do ângulo de disparo
     */
    public final Vector2 getProjectileSpawnPosition(Direction direction) {
        Vector2 base = new Vector2(x, y);

        return base.add(getOffsetForDirection(direction));
    }

    /// Obtém offset da direção
    protected Vector2 getOffsetForDirection(Direction direction) {
        if (direction.isRight()) return getRightOffset();
        if (direction.isLeft()) return getLeftOffset();
        if (direction.isDown()) return getDownOffset();
        if (direction.isUp()) return getUpOffset();

        return new Vector2(); // STILL ou default
    }

    public boolean isRecharging() {
        return recharging;
    }

    // Métodos customizáveis por subclasses
    protected Vector2 getRightOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getLeftOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getDownOffset() {
        return new Vector2(0, 0);
    }

    protected Vector2 getUpOffset() {
        return new Vector2(0, 0);
    }


}
