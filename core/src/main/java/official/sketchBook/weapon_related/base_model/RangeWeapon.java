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

import java.util.EnumMap;
import java.util.Objects;

import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.recharge;
import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.shoot;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Debug.ZERO_V2;
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
    /// Buffer da posição inicial do projétil
    protected final Vector2 spawnPos = new Vector2();

    /// Pré-definições para posição inicial do projétil
    protected final EnumMap<Direction, Vector2> defSpawnPosMap = new EnumMap<>(Direction.class);

    /// Pré-definições para direção de disparo
    protected final EnumMap<Direction, Vector2> defShootDirMap = new EnumMap<>(Direction.class);

    protected RangeWeapon(Class<T> weaponClass, DamageAbleEntity owner, AnchorPoint point) {
        super(weaponClass, owner, point);
        this.projectileEmitter = EmitterRegister.getEmitter(owner);
        Objects.requireNonNull(projectileEmitter, "Emitter must be registered to use this weapon");

        this.initSpriteSheet();
        this.initAnimations();

        this.initDefaultStatus();

        initDefSpawnPos();
        initDefShootPos();

        this.initManagers();
    }

    /// Inicia dentro da classe filha as posições de criação de um projétil
    protected abstract void initDefSpawnPos();

    /// Inicia dentro da classe filha as direções que um projétil pode ser lançado
    protected abstract void initDefShootPos();

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
        updateRenderingOffSets();
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
            (weaponStatus.recoilForce * weaponStatus.recoilForceMultiplier)
        );
    }

    /**
     * Função padrão para disparo de projétil comum
     *
     * @param p Projétil a ser disparado
     */
    protected void shoot(Projectile p) {
        projectileEmitter.fire(
            p,
            projectileSpeed * shootDirection.x,
            projectileSpeed * shootDirection.y,
            1f
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
    protected abstract void updateRenderingOffSets();

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
        return spawnPos.set(x / PPM, y / PPM)
            .add(getDefPosFromSpawnPosMap(direction));
    }

    /// Atualiza o buffer da direção de disparo
    public void setShootDirection(float x, float y) {
        // Atualiza o vetor reutilizável com os novos valores normalizados
        shootDirection.set(x, y);
    }

    /// Atualiza o tipo de projétil do emissor
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

    /**
     * Adiciona mais uma direção de disparo de acordo com uma direção
     *
     * @param dir  direção do disparo como chave
     * @param xDir valor entre 1 e 0 para definir a direção em x
     * @param yDir valor entre 1 e 0 para definir a direção em y
     */
    public final void addDefShotDir(Direction dir, byte xDir, byte yDir) {
        if (xDir < -1 || xDir > 1 || yDir < -1 || yDir > 1) {
            throw new IllegalArgumentException("xDir e yDir devem estar entre -1 e 1");
        }
        defShootDirMap.put(dir, new Vector2(xDir, yDir));
    }

    /**
     * Obtemos a direção do disparo do projétil
     *
     * @param dir Direção usada como chave
     */
    public final Vector2 getDefShootDir(Direction dir) {
        return defShootDirMap.getOrDefault(dir, ZERO_V2);
    }

    /**
     * Adiciona mais uma posição no mapa de posições de início
     *
     * @param dir  Direção que aquela deve ser atrelada
     * @param xPos Posição de spawn x
     * @param yPos Posição de spawn y
     */
    public final void addSpawnPos(Direction dir, float xPos, float yPos) {
        defSpawnPosMap.put(dir, new Vector2(xPos, yPos).scl((1 / PPM)));
    }

    /**
     * Obtemos a posição inicial do projétil desde que esteja dentro da map
     *
     * @param dir Direção usada como chave
     */
    public final Vector2 getDefPosFromSpawnPosMap(Direction dir) {
        return defSpawnPosMap.getOrDefault(dir, ZERO_V2);
    }

}
