package official.sketchBook.weapon_related;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.gameObject_related.util.AnchorPoint;
import official.sketchBook.weapon_related.util.RangeWeaponStatus;
import official.sketchBook.projectiles_related.Projectile;
import official.sketchBook.projectiles_related.projectiles.SlugProjectile;
import official.sketchBook.projectiles_related.projectiles.TestProjectile;
import official.sketchBook.util_related.enumerators.directions.Direction;
import official.sketchBook.util_related.info.paths.WeaponsSpritePath;
import official.sketchBook.weapon_related.base_model.RangeWeapon;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.screen_related.PlayScreen.PPM;
import static official.sketchBook.util_related.info.util.values.AnimationTitles.Weapon.*;

public class Shotgun extends RangeWeapon<Shotgun> {


    public Shotgun(Entity owner, AnchorPoint point) {
        super(Shotgun.class, owner, point);

        updateProjectileIndex(1);

    }

    @Override
    protected void initDefaultStatus() {
        this.weaponStatus = new RangeWeaponStatus(
            2,
            1.5f,
            1f
        );
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    protected void updateOffSets() {
        float x, y;

        if (owner.isFacingForward()) {
            x = -16f;
        } else {
            x = 16f;
        }
        y = -4;

        setRelativeOffset(x, y);

    }

    @Override
    public void updateAnimations() {
        if (!rechargeManager.isRecharging() && owner.isIdle() || !rechargeManager.isRecharging() && owner.isRunning()) {
            aniPlayer.setAnimation(run);
        } else if (rechargeManager.isRecharging()) {
            aniPlayer.setAnimation(recharge);
            aniPlayer.setAutoUpdateAni(true);
            aniPlayer.setAnimationLooping(false);
        }
    }

    @Override
    protected void performShoot() {
        if (!canShoot()) return;

        if (weaponStatus.ammo <= 0) {
            dealEmptyAmmoOnShoot();
            return;
        }

        //Verifica o tipo de projétil e assim executa o tiro correspondente
        if (projectileType.equals(SlugProjectile.class)) {
            slugShot();
        }
        weaponStatus.ammo--;
    }

    @Override
    protected void dealEmptyAmmoOnShoot() {

    }

    /// Tiro único
    private void slugShot() {
        if (!canShoot()) return;

        Projectile p = projectileEmitter.obtain(
            getProjectileSpawnPosition(owner.isFacingForward() ? Direction.RIGHT : Direction.LEFT)
        );

        if (!canShoot()) return;

        // Supondo que você queira disparar a 300 pixels/seg
        float xSpeed = 300f / PPM;
        projectileEmitter.fire(p, owner.isFacingForward() ? xSpeed : -xSpeed, 0, 1f);
    }

    @Override
    public void secondaryUse() {

    }

    @Override
    protected Vector2 getRightOffset() {
        return new Vector2(40, 4);
    }

    @Override
    protected Vector2 getLeftOffset() {
        return new Vector2(-40, 4);
    }

    protected void initAnimations() {
        this.aniPlayer = new ObjectAnimationPlayer();

        aniPlayer.addAnimation(shoot, Arrays.asList(
            new Sprite(0, 0, 0.1f),
            new Sprite(1, 0, 0.1f)
        ));

        aniPlayer.addAnimation(recharge, Arrays.asList(
            new Sprite(1, 2, 0.1f),
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

        aniPlayer.setAnimation(run);
    }

    protected void initSpriteSheet() {
        this.spriteDataHandler = new SpriteSheetDataHandler(
            x,
            y,
            0,
            0,
            3,
            3,
            owner.isFacingForward(),
            false,
            new Texture(WeaponsSpritePath.shotgun_path)
        );
    }

    private void updateProjectileIndex(int projectileIndex) {
        if (projectileIndex == 1) {
            configProjectileTypeOnEmitter(SlugProjectile.class);
        } else if (projectileIndex == 2) {
            configProjectileTypeOnEmitter(TestProjectile.class);
        }
    }
}
