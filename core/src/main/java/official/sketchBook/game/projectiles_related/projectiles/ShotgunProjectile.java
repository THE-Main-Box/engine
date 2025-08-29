package official.sketchBook.game.projectiles_related.projectiles;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.engine.components_related.integration_interfaces.RangeWeaponWielderII;
import official.sketchBook.engine.projectileRelated.model.Projectile;

public class ShotgunProjectile extends Projectile {
    public ShotgunProjectile(World world) {
        super(world);

        this.setLifeTime(5);

        this.initBodyBehavior(
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        );


    }

    @Override
    public void init(RangeWeaponWielderII owner) {
        super.init(owner);
    }

    @Override
    protected void setBodyDefValues() {
        this.radius = 3.4f;
        this.defFric = 0f;
        this.defRest = 0f;
        this.defDens = 0.1f;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }
}
