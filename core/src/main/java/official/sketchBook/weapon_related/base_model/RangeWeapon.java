package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.util_related.util.entity.AnchorPoint;
import official.sketchBook.weapon_related.util.weapon.ShootStateManager;
import official.sketchBook.weapon_related.util.weapon.status.RangeWeaponStatus;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.weapon_related.util.weapon.RechargeManager;
import official.sketchBook.weapon_related.base_model.interfaces.IRangeCapable;

import java.util.Objects;

import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.recharge;
import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.shoot;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public abstract class RangeWeapon<T extends RangeWeapon<T>> extends BaseWeapon<T> implements IRangeCapable {

    protected Class<? extends Projectile> projectileType;
    protected Emitter projectileEmitter;

    protected RangeWeaponStatus weaponStatus;

    protected RechargeManager rechargeManager;
    protected ShootStateManager shootStateManager;

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
        this.rechargeManager = new RechargeManager(
            this,
            this.weaponStatus,
            aniPlayer.getTotalAnimationTime(aniPlayer.getAnimationByKey(recharge)),
            true
        );
        this.rechargeManager.setOnRechargeStartCallback(this::onRechargeStart);
        this.rechargeManager.setOnRechargeFinishedCallback(this::onRechargeEnd);

        this.shootStateManager = new ShootStateManager(
            this,
            this.weaponStatus,
            aniPlayer.getTotalAnimationTime(aniPlayer.getAnimationByKey(shoot)),
            this.weaponStatus.fireCoolDown
        );
        this.shootStateManager.setOnShootCallback(this::performShoot);
    }

    @Override
    public void update(float deltaTime) {
        updateOffSets();
        updateAnimations();

        shootStateManager.update(deltaTime);
        rechargeManager.updateRechargeState(deltaTime);
    }

    @Override
    public void use() {
        shootStateManager.tryToShoot();
    }

    //TODO: implementar um sistema para permitir um comportamento de entrando no estado de recarga e saindo do estado

    /// Permite que a arma implemente uma mecânica de recarga customizada
    public void recharge() {
        rechargeManager.recharge();
    }

    /// Permite uma ação antes de executar a recarga
    protected abstract void onRechargeStart();

    /// Permite uma ação depois da recarga
    protected abstract void onRechargeEnd();

    /// Sistema para lidar com a munição vazia
    protected abstract void dealEmptyAmmoOnShoot();

    /// Inicia os valores padrão dos status de uma arma
    protected abstract void initDefaultStatus();

    /// Permite a alteração do offset gráfico da arma
    protected abstract void updateOffSets();

    /// Permite uma implementação própria de um disparo, executado apenas caso possamos atirar de fato
    protected abstract void performShoot();

    /// Valida para saber se podemos atirar
    public boolean canShoot() {
        return isConfigured() && !rechargeManager.isRecharging();
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

    @Override
    public RechargeManager getRechargeManager() {
        return this.rechargeManager;
    }

    @Override
    public ShootStateManager getShootStateManager() {
        return shootStateManager;
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
