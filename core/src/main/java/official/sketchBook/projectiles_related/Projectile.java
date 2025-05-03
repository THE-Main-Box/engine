package official.sketchBook.projectiles_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.gameObject_related.GameObject;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.util.values.FixtureType;

public abstract class Projectile extends GameObject implements Pool.Poolable {

    protected boolean active;// flag para se está ativo
    protected float lifeTime;//tempo de vida do projétil
    protected float radius;//raio da area da body do projétil
    protected ProjectileControllerComponent controllerComponent;//componente de controle, tem um em cada projétil

    protected Entity owner; //dono do projétil

    public Projectile(float x, float y, float radius, boolean facingForward, World world) {
        super(x, y, radius * 2, radius * 2, facingForward, world);
        this.active = false;
        this.radius = radius;

        this.controllerComponent = new ProjectileControllerComponent(this);
        this.addComponent(controllerComponent);
    }

    public abstract void init(Entity owner);

    protected void initValues(
        Entity owner,
        float lifeTime,
        boolean stickOnCollision,
        boolean stickOnWall,
        boolean stickOnCeiling,
        boolean stickOnGround,
        boolean affectedByGravity
    ) {
        this.active = true;
        this.owner = owner;
        this.setLifeTime(lifeTime);

        this.controllerComponent.setStickOnCollision(stickOnCollision);
        this.controllerComponent.setStickToWall(stickOnWall);
        this.controllerComponent.setStickToCeiling(stickOnCeiling);
        this.controllerComponent.setStickToGround(stickOnGround);
        this.controllerComponent.setAffectedByGravity(affectedByGravity);

        this.controllerComponent.reset();
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
        body.setUserData(new FixtureType(FixtType.PROJECTILE, this));
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
        this.owner = null;

        this.setLifeTime(0f);

        controllerComponent.reset();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
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
