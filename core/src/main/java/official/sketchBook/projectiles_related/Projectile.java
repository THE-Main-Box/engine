package official.sketchBook.projectiles_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDatahandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.util.values.FixtureType;

import java.util.ArrayList;
import java.util.List;

public abstract class Projectile implements Pool.Poolable {

    protected boolean active;// flag para se está ativo
    protected float lifeTime;//tempo de vida do projétil
    protected float radius;//raio da area da body do projétil
    protected ProjectileControllerComponent controllerComponent;//componente de controle, tem um em cada projétil

    protected float x, y;

    protected List<Component> components;

    protected SpriteSheetDatahandler spriteSheetDatahandler;
    protected ObjectAnimationPlayer animationPlayer;

    protected Entity owner; //dono do projétil
    protected Body body;
    protected World world;

    protected boolean facingForward;
    protected float defFric, defRest, defDens;

    public Projectile(World world) {
        this.world = world;
        this.active = false;
        this.components = new ArrayList<>();
        this.controllerComponent = new ProjectileControllerComponent(this);

        this.addComponent(controllerComponent);

        this.setBodyDefValues();
        this.createBody();
    }

    public abstract void init(Entity owner);

    protected void initBodyBehavior(
        boolean stickOnCollision,
        boolean stickOnWall,
        boolean stickOnCeiling,
        boolean stickOnGround,
        boolean affectedByGravity
    ) {
        this.controllerComponent.setStickOnCollision(stickOnCollision);
        this.controllerComponent.setStickToWall(stickOnWall);
        this.controllerComponent.setStickToCeiling(stickOnCeiling);
        this.controllerComponent.setStickToGround(stickOnGround);
        this.controllerComponent.setAffectedByGravity(affectedByGravity);
    }

    public abstract void onEnvironmentCollision(Contact contact, Object target);

    public abstract void onEnvironmentEndCollision(Contact contact, Object target);

    public abstract void onEntityCollision(Contact contact, Object target);

    public abstract void onEntityEndCollision(Contact contact, Object target);

    public abstract void onProjectileCollision(Contact contact, Object target);

    public abstract void onProjectileEndCollision(Contact contact, Object target);

    protected abstract void setBodyDefValues();

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

    public void update(float deltaTime) {
        if (!active) return;

        updateComponents(deltaTime);

    }

    protected void updateComponents(float delta) {
        for (Component component : components) {
            component.update(delta);
        }
    }

    @Override
    public void reset() {
        this.active = false;
        this.owner = null;

        this.setLifeTime(0f);

        controllerComponent.reset();
    }


    public void render(SpriteBatch batch) {

    }

    public boolean shouldBeRecycled() {
        return controllerComponent.getInactiveTime() >= controllerComponent.getInactiveTimeLimit() && !active;
    }

    public void dispose() {
        if (spriteSheetDatahandler != null) {
            spriteSheetDatahandler.dispose();
        }
    }

    public void destroyPhysics(){
        if(body != null && world != null && !world.isLocked()){
            world.destroyBody(body);
            this.body = null;
        }
    }

    public void destroy() {
        destroyPhysics();
        dispose();
        components.clear();
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
        this.controllerComponent.setTimeAliveLimit(lifeTime);
    }

    public ProjectileControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFacingForward() {
        return facingForward;
    }

    public void setFacingForward(boolean facingForward) {
        this.facingForward = facingForward;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void addComponent(Component comp) {
        components.add(comp);
    }

    public <T> T getComponent(Class<T> componentClass) {
        for (Component comp : components) {
            if (componentClass.isInstance(comp)) {
                return componentClass.cast(comp);
            }
        }
        return null;
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return components.stream().anyMatch(type::isInstance);
    }

}
