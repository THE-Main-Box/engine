package official.sketchBook.projectiles_related.projectiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.Projectile;

public class TestProjectile extends Projectile {

    public TestProjectile(World world) {
        super(world);

        this.initBodyBehavior(
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            1f,
            1f

        );

    }

    @Override
    protected void setBodyDefValues() {
        this.radius = 2f;
        this.defFric = 0.1f;
        this.defDens = 0.1f;
        this.defRest = 0f;

    }

    @Override
    public void init(Entity owner) {
        super.init(owner);
        this.setLifeTime(10f);

    }

    @Override
    public void onEnvironmentCollision(Contact contact, Object target) {

    }

    @Override
    public void onEnvironmentEndCollision(Contact contact, Object target) {

    }

    @Override
    public void onEntityCollision(Contact contact, Object target) {

    }

    @Override
    public void onEntityEndCollision(Contact contact, Object target) {

    }

    @Override
    public void onProjectileCollision(Contact contact, Object target) {

    }

    @Override
    public void onProjectileEndCollision(Contact contact, Object target) {

    }
}
