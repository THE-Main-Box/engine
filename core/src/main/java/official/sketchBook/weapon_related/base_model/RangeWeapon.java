package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.gameObject_related.util.AnchorPoint;
import official.sketchBook.weapon_related.util.RangeWeaponStatus;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.weapon_related.util.RechargeManager;

import java.util.Objects;

import static official.sketchBook.screen_related.PlayScreen.PPM;

public abstract class RangeWeapon<T extends RangeWeapon<T>> extends BaseWeapon<T> {

    protected Class<? extends Projectile> projectileType;
    protected Emitter projectileEmitter;

    protected RangeWeaponStatus weaponStatus;

    protected RechargeManager rechargeManager;

    protected RangeWeapon(Class<T> weaponClass, Entity owner, AnchorPoint point) {
        super(weaponClass, owner, point);
        this.projectileEmitter = EmitterRegister.getEmitter(owner);
        Objects.requireNonNull(projectileEmitter, "Emitter must be registered to use this weapon");

        this.initSpriteSheet();
        this.initAnimations();

        this.initDefaultStatus();

        this.initManagers();
    }

    protected void initManagers() {
        this.rechargeManager = new RechargeManager(this);
    }

    @Override
    public void update(float deltaTime) {
        updateOffSets();
        updateAnimations();

        rechargeManager.updateRechargeState(deltaTime);
    }

    @Override
    public void use() {
        performShoot();
    }

    /// Permite que a arma implemente uma mecânica de recarga customizada
    public void recharge() {
        rechargeManager.recharge();
    }

    //TODO:Implementar efeito sonoro de arma vazia

    /// Sistema para lidar com a munição vazia
    protected abstract void dealEmptyAmmoOnShoot();

    /// Inicia os valores padrão dos status de uma arma
    protected abstract void initDefaultStatus();

    /// Executa o código relacionado ao disparo da arma
    protected abstract void performShoot();

    /// Permite a alteração do offset gráfico da arma
    protected abstract void updateOffSets();


    /// Valida para saber se podemos atirar
    protected boolean canShoot() {
        return isConfigured() && (!rechargeManager.isRecharging());
    }

    /// Valida se estamos configurados
    protected boolean isConfigured() {
        return weaponStatus != null && projectileEmitter != null && projectileEmitter.isConfigured();
    }

    /**
     * Ponto de disparo com base na direção passada.     *
     *
     * @param direction Enumerador do tipo direção, é passado como identificador do ângulo de disparo
     */
    public final Vector2 getProjectileSpawnPosition(Direction direction) {
        Vector2 base = new Vector2(x / PPM, y / PPM);

        return base.add(getOffsetForDirection(direction));
    }

    /// Obtém offset da direção
    protected Vector2 getOffsetForDirection(Direction direction) {
        if (direction.isRight()) return getRightOffset().scl(1f / PPM);
        if (direction.isLeft()) return getLeftOffset().scl(1f / PPM);
        if (direction.isDown()) return getDownOffset().scl(1f / PPM);
        if (direction.isUp()) return getUpOffset().scl(1f / PPM);

        return new Vector2(); // STILL ou default
    }

    protected void configProjectileTypeOnEmitter(Class<? extends Projectile> type) {
        this.projectileType = type;
        this.projectileEmitter.configure(type);
    }

    //Getters e setters//
    public RangeWeaponStatus getWeaponStatus() {
        return weaponStatus;
    }

    public void setWeaponStatus(RangeWeaponStatus weaponStatus) {
        this.weaponStatus = weaponStatus;
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
