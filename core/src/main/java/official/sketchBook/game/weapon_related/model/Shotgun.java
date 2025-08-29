package official.sketchBook.game.weapon_related.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import official.sketchBook.engine.animation_related.ObjectAnimationPlayer;
import official.sketchBook.engine.animation_related.Sprite;
import official.sketchBook.engine.animation_related.SpriteSheetDataHandler;
import official.sketchBook.engine.gameObject_related.base_model.RangeWeaponWieldingEntity;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.game.projectiles_related.projectiles.ShotgunProjectile;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;
import official.sketchBook.game.util_related.info.paths.WeaponsSpritePath;
import official.sketchBook.game.util_related.util.entity.AnchorPoint;
import official.sketchBook.engine.weapon_related.base_model.RangeWeapon;
import official.sketchBook.engine.weapon_related.util.status.RangeWeaponStatus;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.game.util_related.info.values.AnimationTitles.Weapon.*;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;
import static official.sketchBook.game.util_related.info.values.constants.RangeWeaponsStatusConstants.Shotgun.*;

public class Shotgun extends RangeWeapon<Shotgun> {

    private static final float slugSpeed = 400 / PPM;

    public Shotgun(RangeWeaponWieldingEntity owner, AnchorPoint point) {
        super(Shotgun.class, owner, point);

        updateProjectileIndex(1);
    }

    @Override
    protected void initDefaultStatus() {
        this.weaponStatus = new RangeWeaponStatus(
            maxAmmo,
            ammoCost,
            fireCooldown,
            fireRecoilSpeed,
            rechargeSpeedMulti,
            fireCooldownSpeedMulti,
            fireRecoilForceMulti
        );
    }

    @Override
    protected void initDefSpawnPos() {
        // posições de spawn nas duas direções //
        addSpawnPos(Direction.LEFT, -42, 5);
        addSpawnPos(Direction.RIGHT, 42, 5);
        // a direção para baixo será dividida em duas, diagonal esquerda e direita
        // essa decisão servirá não porque estamos trabalhando com diagonais em si,
        //mas para evitar a necessidade de aplicar valores no momento do disparo,
        // o que pode causar uma arquitetura meio quebrada,
        // iremos separar pelo enum,
        // assim facilitando a integração de mais direções
        addSpawnPos(Direction.DOWN_LEFT, -11, -22);
        addSpawnPos(Direction.DOWN_RIGHT, 11, -22);
    }

    @Override
    protected void initDefShootPos() {
        addDefShotDir(Direction.LEFT, (byte) -1, (byte) 0);
        addDefShotDir(Direction.RIGHT, (byte) 1, (byte) 0);
        addDefShotDir(Direction.DOWN, (byte) 0, (byte) -1);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

    }

    @Override
    protected void onRechargeStart() {
    }

    @Override
    protected void onRechargeEnd() {

    }

    protected void updateRenderingOffSets() {
        float xOffSet, yOffSet;

        yOffSet = -4;

        //Se estiver mirando pra baixo
        if (canPogoShoot()) {
            // Mira para baixo
            spriteDataHandler.setRotation(owner.isxAxisInverted() ? -90 : 90);
            xOffSet = owner.isxAxisInverted() ? -10f : 10f;
        } else {//se não estiver mirando
            // Mira para frente (nem cima nem baixo)
            spriteDataHandler.setRotation(0);
            xOffSet = owner.isxAxisInverted() ? -16f : 16f;
        }

        if (rechargeManager.isRecharging()) {
            spriteDataHandler.setRotation(0);
        }

        setRelativeOffset(xOffSet, yOffSet);
    }


    @Override
    public void updateAnimations() {
        if (shootStateManager.isShooting()) return;

        //Se estivermos recarregando
        if (rechargeManager.isRecharging()) {
            aniPlayer.playAnimation(recharge);
            aniPlayer.setAutoUpdateAni(true);
            aniPlayer.setAnimationLooping(false);
        } else {// Se não estivermos recarregando, mas estivermos andando ou parados sem fazer nada
            if (owner.isRunning() || owner.isIdle()) {
                aniPlayer.playAnimation(run);
            }
        }
    }

    @Override
    public void performShoot() {
        if (weaponStatus.ammo <= 0) {
            dealEmptyAmmoOnShoot();
            return;
        }

        //Determinamos a posição em que iremos disparar com base em alguns fatores
        Direction posOnDirection;//Chave que referencia a posição do projétil
        Direction projDirection;//Chave que referencia a direção que o projétil deve percorrer
        if (canPogoShoot()) {
            posOnDirection = owner.isxAxisInverted() ? Direction.DOWN_RIGHT : Direction.DOWN_LEFT;
            projDirection = Direction.DOWN;
        } else {
            posOnDirection = owner.isxAxisInverted() ? Direction.RIGHT : Direction.LEFT;
            projDirection = posOnDirection;
        }

        //Obtemos a direção que o projétil deve ser lançado
        Vector2 direction = getDefShootDir(projDirection);

        //Passamos a direção que o projétil deve ir
        setShootDirection(
            direction.x,//mirando para direita ou esquerda
            direction.y //mirando para cima ou para baixo
        );

        //Verifica o tipo de projétil e assim executa o tiro correspondente
        if (projectileType.equals(ShotgunProjectile.class)) {
            slugShot(posOnDirection);//Passamos a posição de disparo
        }

        aniPlayer.playAnimation(shoot);
        aniPlayer.setAutoUpdateAni(true);
        aniPlayer.setAnimationLooping(false);
        aniPlayer.setAniTick(0);
    }

    @Override
    protected void dealEmptyAmmoOnShoot() {

    }

    /// Tiro único (slug)
    private void slugShot(Direction dir) {
        if (!canShoot()) return;

        Projectile p = projectileEmitter.obtain(
            getProjectileSpawnPosition(dir)
        );

        // Supondo que você queira disparar a 400 pixels/seg
        projectileSpeed = slugSpeed;

        if(!owner.isOnGround()){
            weaponStatus.recoilForceMultiplier = 1;
            System.out.println("jump dash " + !owner.isOnGround());
        } else {
            weaponStatus.recoilForceMultiplier = 0;
            System.out.println("jump dash " + !owner.isOnGround());
        }

        System.out.println(owner.getMoveC().getxSpeed());

        shoot(p);
        applyRecoil(shootDirection);
    }

    @Override
    public void secondaryUse() {

    }

    protected void initAnimations() {
        this.aniPlayer = new ObjectAnimationPlayer();

        aniPlayer.addAnimation(shoot, Arrays.asList(
            new Sprite(0, 0, 0.1f),
            new Sprite(1, 0, 0.05f),
            new Sprite(1, 2, 0.1f)
        ));

        aniPlayer.addAnimation(recharge, Arrays.asList(
            new Sprite(2, 0, 0.08f),
            new Sprite(0, 1, 0.1f),
            new Sprite(1, 1, 0.1f),
            new Sprite(2, 1, 0.2f),
            new Sprite(0, 2, 0.1f),
            new Sprite(1, 2, 0.1f)
        ));

        aniPlayer.addAnimation(run, List.of(
            new Sprite(1, 2)
        ));

        aniPlayer.playAnimation(run);
    }

    protected void initSpriteSheet() {
        this.spriteDataHandler = new SpriteSheetDataHandler(
            x,
            y,
            0,
            0,
            3,
            3,
            owner.isxAxisInverted(),
            owner.isyAxisInverted(),
            new Texture(WeaponsSpritePath.shotgun_path)
        );
    }

    private void updateProjectileIndex(int projectileIndex) {
        if (projectileIndex == 1) {
            configProjectileTypeOnEmitter(ShotgunProjectile.class);
        }
    }

    protected boolean canPogoShoot() {
        return owner.getWeaponWC().isAimingDown() && !owner.isOnGround();
    }
}
