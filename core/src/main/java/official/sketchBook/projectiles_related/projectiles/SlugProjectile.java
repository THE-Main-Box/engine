package official.sketchBook.projectiles_related.projectiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;

public class SlugProjectile extends Projectile {
    public SlugProjectile(World world) {
        super(world);

        this.setAndUpdateRotation(30);

        this.setLifeTime(100);

        this.initBodyBehavior(
            true,
            false,
            false,
            false,
            true,
            false,
            0f,
            0f
        );
    }

    @Override
    public void init(Entity owner) {
        super.init(owner);
    }

    @Override
    public void onEnvironmentCollision(Contact contact, Object target) {

    }

    @Override
    public void onEnvironmentEndCollision(Contact contact, Object target) {

    }

    @Override
    public void onEntityCollision(Contact contact, Entity entity) {

    }

    @Override
    public void onEntityEndCollision(Contact contact, Entity entity) {

    }

    @Override
    public void onProjectileCollision(Contact contact, Projectile projectile) {

    }

    @Override
    public void onProjectileEndCollision(Contact contact, Projectile projectile) {

    }

    @Override
    protected void setBodyDefValues() {
        this.radius = 2f;
        this.defFric = 0.1f;
        this.defDens = 0.1f;
        this.defRest = 0f;
    }

}
