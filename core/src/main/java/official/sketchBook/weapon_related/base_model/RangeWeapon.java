package official.sketchBook.weapon_related.base_model;

import com.badlogic.gdx.math.Vector2;
import official.sketchBook.gameObject_related.base_model.DamageAbleEntity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.emitters.Emitter;
import official.sketchBook.projectiles_related.util.ProjectilePool;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.helpers.HelpMethods;
import official.sketchBook.util_related.registers.EmitterRegister;
import official.sketchBook.util_related.util.entity.AnchorPoint;
import official.sketchBook.weapon_related.base_model.interfaces.IRangeCapable;
import official.sketchBook.weapon_related.util.RechargeManager;
import official.sketchBook.weapon_related.util.ShootStateManager;
import official.sketchBook.weapon_related.util.status.RangeWeaponStatus;

import java.util.Objects;

import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.recharge;
import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.shoot;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;

public abstract class RangeWeapon<T extends RangeWeapon<T>> extends BaseWeapon<T> implements IRangeCapable {

    /// Tipo de projétil
    protected Class<? extends Projectile> projectileType;

    /// Emissor de projétil
    protected Emitter projectileEmitter;

    /// Status de configuração de arma
    protected RangeWeaponStatus weaponStatus;

    /// Gerência do sistema de recarga
    protected RechargeManager rechargeManager;

    /// Gerencia do sistema de disparo
    protected ShootStateManager shootStateManager;

    /// Velocidade do projétil
    protected float projectileSpeed;

    /// Buffer para direção de disparo
    protected final Vector2 shootDirection = new Vector2();

    /// Buffer para posição inicial de projéteis
    private final Vector2 cachedSpawnPos = new Vector2();

    /// Offset padrão para projéteis do lado esquerdo
    protected final Vector2 leftOffSet = new Vector2(0, 0);

    /// Offset padrão para projéteis do lado direito
    protected final Vector2 rightOffSet = new Vector2(0, 0);

    /// Offset padrão para projéteis do lado de cima
    protected final Vector2 upOffSet = new Vector2(0, 0);

    /// Offset padrão para projéteis do lado de baixo
    protected final Vector2 downOffSet = new Vector2(0, 0);

    protected RangeWeapon(Class<T> weaponClass, DamageAbleEntity owner, AnchorPoint point) {
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
        shootStateManager.update(deltaTime);
        rechargeManager.updateRechargeState(deltaTime);
    }

    @Override
    protected void updateRenderVariables() {
        super.updateRenderVariables();
        updateOffSets();
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
     * Ponto de início do disparo com base na direção passada
     *
     * @param direction Enumerador do tipo direção, é passado como identificador do ângulo de disparo
     */
    public final Vector2 getProjectileSpawnPosition(Direction direction) {
        return cachedSpawnPos.set(x / PPM, y / PPM)
            .add(getOffsetForDirection(direction));
    }

    /// Obtém offset da direção
    protected Vector2 getOffsetForDirection(Direction direction) {
        if (direction.isRight()) return getRightOffSet().scl(1f / PPM);
        if (direction.isLeft()) return getLeftOffSet().scl(1f / PPM);
        if (direction.isDown()) return getDownOffSet().scl(1f / PPM);
        if (direction.isUp()) return getUpOffSet().scl(1f / PPM);

        return shootDirection; // STILL ou default
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
    protected Vector2 getLeftOffSet() {
        return leftOffSet;
    }

    protected Vector2 getRightOffSet() {
        return rightOffSet;
    }

    protected Vector2 getUpOffSet() {
        return upOffSet;
    }

    protected Vector2 getDownOffSet() {
        return downOffSet;
    }

    protected void setLeftOffSet(float x, float y){
        leftOffSet.set(x,y);
    }
    protected void setRightOffSet(float x, float y){
        rightOffSet.set(x,y);
    }
    protected void setUpOffSet(float x, float y){
        upOffSet.set(x,y);
    }
    protected void setDownOffSet(float x, float y){
        downOffSet.set(x,y);
    }

}
