package official.sketchBook.projectiles_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.util_related.enumerators.types.FixtureType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;

public abstract class Projectile extends GameObject implements Pool.Poolable {

    protected boolean active;
    protected float lifeTime;
    protected float radius;
    protected ProjectileControllerComponent controllerComponent;

    public Projectile(float x, float y, float radius, boolean facingForward, World world) {
        super(x, y, radius * 2, radius * 2, facingForward, world);
        this.active = false;
        this.radius = radius;
        this.lifeTime = 0;

        this.controllerComponent = new ProjectileControllerComponent(this, lifeTime);
        this.addComponent(controllerComponent);
    }

    public abstract void onEnvironmentCollision(Contact contact, Object target);

    public abstract void onEnvironmentEndCollision(Contact contact, Object target);

    public abstract void onEntityCollision(Contact contact, Object target);

    public abstract void onEntityEndCollision(Contact contact, Object target);

    public abstract void onProjectileCollision(Contact contact, Object target);

    public abstract void onProjectileEndCollision(Contact contact, Object target);

    @Override
    protected void setBodyDefValues() {
        this.defDens = 0.1f;
        this.defFric = 0.1f;
        this.defRest = 0.1f;
    }

    @Override
    protected void createBody() {
        body = BodyCreatorHelper.createCircle(
            world,
            new Vector2(x, y),
            radius,
            BodyDef.BodyType.DynamicBody,
            0.1f, // density
            0f,   // friction
            0f    // restitution
        );
        body.setUserData(new FixtureType(FixtureType.Type.PROJECTILE, this));
        body.setBullet(true); // Importante para colisões de alta velocidade
        body.setFixedRotation(true);
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;
        updateComponents(deltaTime);
    }

    @Override
    public void reset() {
        this.active = false;
        this.setLifeTime(0f);

        controllerComponent.reset();

        // Opcional: resetar posição e corpo físico se necessário
        if (body != null) {
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            body.setTransform(x, y, 0);
            body.setActive(false); // Desativa o corpo no mundo físico
        }
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public float getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
        this.controllerComponent.setLifeTime(lifeTime);
    }

    public ProjectileControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
