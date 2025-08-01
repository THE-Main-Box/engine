package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.gameObject_related.base_model.ArmedEntity;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.util.ProjectilePool;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.util.entity.AnchorPoint;
import official.sketchBook.weapon_related.util.ShootStateManager;
import official.sketchBook.weapon_related.util.status.RangeWeaponStatus;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.weapon_related.util.RechargeManager;
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

    protected final Vector2 shootDirection = new Vector2();
    protected float projectileSpeed;

    protected RangeWeapon(Class<T> weaponClass, ArmedEntity owner, AnchorPoint point) {
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

    /**
     * Aplica uma força de recuo no dono da arma com base na direção e nos multiplicadores.
     *
     * @param direction Vetor de direção do tiro (espera-se um vetor unitário ou normalizável)
     */
    protected void applyRecoil(Vector2 direction) {
        if (direction.isZero()) return;

        HelpMethods.applyRecoil(
            owner.getBody(),
            direction,
            weaponStatus.recoilForce * weaponStatus.recoilForceMultiplier
        );
    }


    @Override
    public void use() {
        shootStateManager.tryToShoot();
    }

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
        ProjectilePool<?> pool = projectileEmitter.getPool().getPoolOf(projectileType);

        return isConfigured()
            && !rechargeManager.isRecharging()
            && (pool == null || pool.canSpawnNewProjectile());
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
        return new Vector2(x / PPM, y / PPM)
            .add(getOffsetForDirection(direction));
    }

    /// Obtém offset da direção
    protected Vector2 getOffsetForDirection(Direction direction) {
        if (direction.isRight()) return getRightOffset().scl(1f / PPM);
        if (direction.isLeft()) return getLeftOffset().scl(1f / PPM);
        if (direction.isDown()) return getDownOffset().scl(1f / PPM);
        if (direction.isUp()) return getUpOffset().scl(1f / PPM);

        return new Vector2(); // STILL ou default
    }

    public void setShootDirection(float x, float y) {
        // Atualiza o vetor reutilizável com os novos valores normalizados
        shootDirection.set(x, y);
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
