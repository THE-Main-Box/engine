package official.sketchBook.projectiles_related.projectiles;

import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;

public class SlugProjectile extends Projectile {
    public SlugProjectile(World world) {
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
    public void init(Entity owner) {
        super.init(owner);
    }

    @Override
    protected void setBodyDefValues() {
        this.radius = 3.4f;
        this.defFric = 0f;
        this.defRest = 0f;
        this.defDens = 0.1f;
    }

}
