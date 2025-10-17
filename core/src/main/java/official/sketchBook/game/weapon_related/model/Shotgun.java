package official.sketchBook.game.weapon_related.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import official.sketchBook.engine.animation_related.ObjectAnimationPlayer;
import official.sketchBook.engine.animation_related.Sprite;
import official.sketchBook.engine.animation_related.SpriteSheetDataHandler;
import official.sketchBook.engine.components_related.integration_interfaces.DamageDealerII;
import official.sketchBook.engine.components_related.integration_interfaces.DamageDealerOwnerII;
import official.sketchBook.engine.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.engine.components_related.integration_interfaces.GroundInteractableII;
import official.sketchBook.engine.gameObject_related.base_model.RangeWeaponWieldingEntity;
import official.sketchBook.engine.projectileRelated.model.Projectile;
import official.sketchBook.engine.util_related.enumerators.directions.Direction;
import official.sketchBook.engine.util_related.enumerators.type.ObjectType;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.RawDamageData;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.point.AnchorPoint;
import official.sketchBook.engine.util_related.utils.general.GameObjectTag;
import official.sketchBook.engine.weapon_related.base_model.RangeWeapon;
import official.sketchBook.engine.weapon_related.util.status.RangeWeaponStatus;
import official.sketchBook.game.projectiles_related.projectiles.ShotgunProjectile;
import official.sketchBook.game.util_related.info.paths.WeaponsSpritePath;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getFromBodyTag;
import static official.sketchBook.engine.util_related.utils.general.HelpMethods.getFromFixtureTag;
import static official.sketchBook.game.util_related.info.values.AnimationTitles.Weapon.*;
import static official.sketchBook.game.util_related.info.values.constants.GameConstants.Physics.PPM;
import static official.sketchBook.game.util_related.info.values.constants.RangeWeaponsStatusConstants.Shotgun.*;

public class Shotgun extends RangeWeapon<Shotgun> implements DamageDealerII {

    private static final float SLUG_SPEED = 400 / PPM;

    private final Vector2 tmpStartRay = new Vector2();
    private final Vector2 tmpEndRay = new Vector2();

    private boolean weaponBlocked;

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
        addSpawnPos(Direction.LEFT, -42, 5);
        addSpawnPos(Direction.RIGHT, 42, 5);
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

    @Override
    protected void updateRenderingOffSets() {
        float xOffSet = owner.isxAxisInverted() ? 16f : -16f;
        float yOffSet = -4;
        float rotation = 0;

        if (canPogoShoot()) {
            rotation = owner.isxAxisInverted() ? 90 : -90;
            xOffSet = owner.isxAxisInverted() ? 10f : -10f;
        }

        if (rechargeManager.isRecharging()) rotation = 0;

        setRelativeOffset(xOffSet, yOffSet);
        spriteDataHandler.setRotation(rotation);
    }

    @Override
    public void updateAnimations() {
        if (shootStateManager.isShooting()) return;

        if (rechargeManager.isRecharging()) {
            aniPlayer.playAnimation(recharge);
            aniPlayer.setAutoUpdateAni(true);
            aniPlayer.setAnimationLooping(false);
        } else if (owner.isMoving() || owner.isIdle()) {
            aniPlayer.playAnimation(run);
        }
    }

    @Override
    protected void performShoot() {
        if (weaponStatus.ammo <= 0) {
            dealEmptyAmmoOnShoot();
            return;
        }
        if (!canShoot()) return;

        Direction posOnDir = Direction.STILL;
        Direction shootDir = Direction.STILL;

        if (canPogoShoot()) {
            posOnDir = owner.getWeaponWC().isAimingLeft() ? Direction.DOWN_LEFT : Direction.DOWN_RIGHT;
            shootDir = Direction.DOWN;
        } else {
            if (owner.getWeaponWC().isAimingLeft()) {
                posOnDir = Direction.LEFT;
                shootDir = Direction.LEFT;
            } else if (owner.getWeaponWC().isAimingRight()) {
                posOnDir = Direction.RIGHT;
                shootDir = Direction.RIGHT;
            }
        }

        setShootDirection(getDefShootDir(shootDir));
        getProjectileSpawnPosition(posOnDir);

        updateRayCast();

        aniPlayer.playAnimation(shoot);
        aniPlayer.setAutoUpdateAni(true);
        aniPlayer.setAnimationLooping(false);
        aniPlayer.setAniTick(0);
    }

    private void updateRayCast() {
        if (rayCastHelper == null) return;

        tmpStartRay.set(this.x / PPM, this.y / PPM);
        tmpEndRay.set(spawnPos);

        weaponBlocked = false;

        rayCastHelper.castRay(tmpStartRay, tmpEndRay, false, data -> {
            if (data != null) {
                GameObjectTag tag = getFromBodyTag(data.fixture());
                if (tag != null && tag.type().equals(ObjectType.ENVIRONMENT)) {
                    applyRecoilOnShoot();
                    weaponBlocked = true;
                }
            }
        });

        rayCastHelper.castRaySensorsOnly(tmpStartRay, tmpEndRay, data -> {
            if (data != null) {
                weaponBlocked = true;
                GameObjectTag tag = getFromFixtureTag(data.fixture());
                if (tag != null && tag.owner() instanceof DamageReceiverII receiver
                    && tag.type() == ObjectType.HURTBOX) {
                    receiver.getDamageReceiveC().damage(ShotgunProjectile.data, this);
                    applyRecoilOnShoot();
                }
            }
        });

        if (projectileType.equals(ShotgunProjectile.class) && !weaponBlocked) {
            slugShot(spawnPos);
        }
    }

    @Override
    protected void dealEmptyAmmoOnShoot() {
    }

    private void applyRecoilOnShoot() {
        float multiplier = canPogoShoot() ?
            (
                weaponBlocked
                    ? 1.5f // se a arma estiver bloqueada
                    : 1f // se não estiver
            ) : 0;// se não pudermos executar o pogo

        weaponStatus.recoilForceMultiplier = multiplier;

        if (multiplier != 0f) applyRecoil(shootDirection);
    }

    private void slugShot(Vector2 dir) {
        Projectile p = projectileEmitter.obtain(dir);
        projectileSpeed = SLUG_SPEED;
        applyRecoilOnShoot();
        shoot(p);
    }

    @Override
    public void secondaryUse() {
    }

    @Override
    protected void initAnimations() {
        aniPlayer = new ObjectAnimationPlayer();

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

        aniPlayer.addAnimation(run, List.of(new Sprite(1, 2)));
        aniPlayer.playAnimation(run);
    }

    @Override
    protected void initSpriteSheet() {
        spriteDataHandler = new SpriteSheetDataHandler(
            x, y, 0, 0, 3, 3,
            owner.isxAxisInverted(),
            owner.isyAxisInverted(),
            new Texture(WeaponsSpritePath.shotgun_path)
        );
    }

    private void updateProjectileIndex(int projectileIndex) {
        if (projectileIndex == 1) configProjectileTypeOnEmitter(ShotgunProjectile.class);
    }

    protected boolean canPogoShoot() {
        if (!owner.getWeaponWC().isAimingDown()) return false;
        if (!(owner instanceof GroundInteractableII gOwner)) return false;
        return !gOwner.isOnGround();
    }

    @Override
    public DamageDealerOwnerII getOwner() {
        return owner;
    }

    @Override
    public Body getBody() {
        return owner.getBody();
    }

    @Override
    public RawDamageData getDamageData() {
        return ShotgunProjectile.data;
    }

    @Override
    public boolean isDamageAble() {
        return true;
    }
}
