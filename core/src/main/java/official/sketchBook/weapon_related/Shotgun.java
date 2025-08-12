package official.sketchBook.weapon_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.gameObject_related.base_model.DamageAbleEntity;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.projectiles.ShotgunProjectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.paths.WeaponsSpritePath;
import official.sketchBook.util_related.util.entity.AnchorPoint;
import official.sketchBook.weapon_related.base_model.RangeWeapon;
import official.sketchBook.weapon_related.util.status.RangeWeaponStatus;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.util_related.info.values.AnimationTitles.Weapon.*;
import static official.sketchBook.util_related.info.values.constants.GameConstants.Physics.PPM;
import static official.sketchBook.util_related.info.values.constants.RangeWeaponsStatusConstants.Shotgun.*;

public class Shotgun extends RangeWeapon<Shotgun> {

    public Shotgun(DamageAbleEntity owner, AnchorPoint point) {
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
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(canPogoShoot()) {
            this.weaponStatus.recoilForceMultiplier = 1f;
        } else {
            this.weaponStatus.recoilForceMultiplier = 0;
        }

    }

    @Override
    protected void onRechargeStart() {
    }

    @Override
    protected void onRechargeEnd() {

    }

    protected void updateOffSets() {
        float x, y;

        y = -4;

        //Se estiver mirando pra baixo
        if (canPogoShoot()) {
            // Mira para baixo
            spriteDataHandler.setRotation(owner.isxAxisInverted() ? -90 : 90);
            x = owner.isxAxisInverted() ? -10f : 10f;
        } else {//se não estiver mirando
            // Mira para frente (nem cima nem baixo)
            spriteDataHandler.setRotation(0);
            x = owner.isxAxisInverted() ? -16f : 16f;
        }

        setRelativeOffset(x, y);
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
        Direction dir;
        if (canPogoShoot()) {
            dir = Direction.DOWN;
        } else {
            dir = owner.isxAxisInverted() ? Direction.RIGHT : Direction.LEFT;
        }

        //Determinamos a direção da movimentação do projétil
        int xDir = 0;
        int yDir = 0;
        if (canPogoShoot()) {
            yDir = -1;
        } else {
            xDir = owner.isxAxisInverted() ? 1 : -1;
        }

        //Passamos a direção que o projétil deve ir
        setShootDirection(
            xDir,//mirando para direita ou esquerda
            yDir //mirando para cima ou para baixo
        );

        //Verifica o tipo de projétil e assim executa o tiro correspondente
        if (projectileType.equals(ShotgunProjectile.class)) {
            slugShot(dir);
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

        // Supondo que você queira disparar a 300 pixels/seg
        projectileSpeed = 400f / PPM;

        shoot(p);
        applyRecoil(shootDirection);
    }

    @Override
    public void secondaryUse() {

    }

    private void shoot(Projectile p) {
        projectileEmitter.fire(
            p,
            projectileSpeed * shootDirection.x,
            projectileSpeed * shootDirection.y,
            1f
        );

    }

    @Override
    protected Vector2 getRightOffSet() {
        setRightOffSet(42, 5);
        return super.getRightOffSet();
    }

    @Override
    protected Vector2 getLeftOffSet() {
        setLeftOffSet(-42, 5);
        return super.getLeftOffSet();
    }

    @Override
    protected Vector2 getDownOffSet() {
        setDownOffSet(owner.isxAxisInverted()? 11 : -10, -22);
        return super.getDownOffSet();
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

    protected boolean canPogoShoot(){
        return owner.getWeaponWC().isAimingDown() && !owner.isOnGround();
    }
}
