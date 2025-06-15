package official.sketchBook.weapon_related;

import com.badlogic.gdx.graphics.Texture;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.projectiles_related.projectiles.SlugProjectile;
import official.sketchBook.projectiles_related.projectiles.TestProjectile;
import official.sketchBook.util_related.info.paths.WeaponsSpritePath;

import java.util.Arrays;
import java.util.List;

import static official.sketchBook.util_related.info.util.values.AnimationTitles.*;

public class Shotgun extends RangeWeapon {

    public Shotgun(Entity owner) {
        super(owner);

        updateProjectileIndex(1);
        updateRechargeTimeLimit();

    }

    @Override
    public void update(float deltaTime) {
        updateOffSets();
        updateAnimations();

        updateRechargingTime(deltaTime);
    }

    private void updateRechargingTime(float delta) {
        rechargingTimeLimit.update(delta);

        if (rechargingTimeLimit.isRunning()) {
            if (rechargingTimeLimit.isFinished()) {
                rechargingTimeLimit.stop();
                rechargingTimeLimit.reset();
                recharging = false;
            }
        }
    }

    private void updateRechargeTimeLimit() {
        if (rechargingTimeLimit == null) {
            rechargingTimeLimit = new TimerComponent(
                aniPlayer.getTotalAnimationTime(aniPlayer.getAnimationByKey(recharge))
            );
        } else {
            rechargingTimeLimit.setTargetTime(
                aniPlayer.getTotalAnimationTime(aniPlayer.getAnimationByKey(recharge))
            );
        }
    }

    private void updateOffSets() {
        float x, y;

        if (owner.isFacingForward()) {
            x = (spriteDataHandler.getCanvasWidth() / 2f) - 16.5f;
        } else {
            x = (spriteDataHandler.getCanvasWidth() / 2f) + - 16.5f;
        }
        y = (spriteDataHandler.getCanvasHeight() / 2f) - 4;

        this.xOffset = x;
        this.yOffset = y;
    }

    @Override
    public void updateAnimations() {
        if (!recharging && owner.isOnGround() && owner.isMoving()) {
            aniPlayer.setAnimation(run);
        } else if (!recharging && owner.isOnGround() && !owner.isMoving()) {
            aniPlayer.setAnimation(idle);
        } else if (recharging) {
            aniPlayer.setAnimation(recharge);
        }
    }

    @Override
    public void recharge() {
        this.recharging = true;
        this.rechargingTimeLimit.start();
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
            new Sprite(0, 2, 0.08f),
            new Sprite(1, 2, 0.1f)
        ));

        aniPlayer.addAnimation(idle, Arrays.asList(
            new Sprite(1, 2, 0.3f),
            new Sprite(2, 2, 0.3f)
        ));

        aniPlayer.addAnimation(run, List.of(
            new Sprite(1, 2)
        ));

        aniPlayer.setAnimation(idle);
    }

    protected void initSpriteSheet() {
        this.spriteDataHandler = new SpriteSheetDataHandler(
            owner.getX(),
            owner.getY(),
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
