package official.sketchBook.projectiles_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.components_related.toUse_component.projectile.ProjectilePhysicsComponent;
import official.sketchBook.gameObject_related.Entity;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.util.values.FixtureType;

import java.util.ArrayList;
import java.util.List;

public abstract class Projectile implements Pool.Poolable {

    //TODO:adicionar compatibilidade para rotação, tanto na renderização quando nos objetos

    protected boolean active;// flag para se está ativo
    protected float lifeTime;//tempo de vida do projétil
    protected float radius = 0;//raio da area da body do projétil

    protected ProjectileControllerComponent controllerComponent;//componente de controle, tem um em cada projétil

    /// Componente de física próprio do projétil
    private final ProjectilePhysicsComponent physicsComponent;
    protected float x, y;

    protected List<Component> components;

    protected SpriteSheetDataHandler spriteSheetDatahandler;
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

        this.setBodyDefValues();
        this.createBody();

        this.controllerComponent = new ProjectileControllerComponent(this);
        this.addComponent(controllerComponent);

        this.physicsComponent = new ProjectilePhysicsComponent(this);
        this.addComponent(physicsComponent);
    }

    /** Inicia as informações dinâmicas do projétil
     * é preciso passar o tempo de vida e outros valores que precisam ser iniciados dinamicamente
     * */
    public void init(Entity owner){
        this.owner = owner;
        this.setActive(true);
    }

    /**
     * Inicia o comportamento padrão do projétil(chamado no construtor)
     *
     * @param stickOnCollision            trava todos os eixos quando detectamos uma colisão
     * @param stickOnWall                 trava o eixo X quando detectamos uma colisão na horizontal
     * @param stickOnCeiling              trava o eixo Y quando detectamos uma colisão vinda da parte de cima do projétil
     * @param stickOnGround               trava o eixo Y quando detectamos uma colisão vinda da parte de baixo do projétil
     * @param affectedByGravity           se o projétil é afetado ou não pela constante da gravidade
     * @param collideWithItself           se o projétil pode colidir com outros do mesmo tipo que o dele
     * @param collideWithOtherProjectiles se o projétil pode colidir com outros projéteis que não são do mesmo tipo
     * @param bXEnv                       constante de restituição do eixo X pra ambiente
     * @param bYEnv                       constante de restituição do eixo Y pra ambiente
     * @param bXEnt                       constante de restituição do eixo X pra entidades
     * @param bYEnt                       constante de restituição do eixo Y pra entidades
     * @param bXProj                      constante de restituição do eixo X pra projéteis
     * @param bYProj                      constante de restituição do eixo Y pra projéteis
     */
    protected void initBodyBehavior(
        boolean stickOnCollision,
        boolean stickOnWall,
        boolean stickOnCeiling,
        boolean stickOnGround,
        boolean affectedByGravity,
        boolean collideWithItself,
        boolean collideWithOtherProjectiles,
        float bXEnv,
        float bYEnv,
        float bXEnt,
        float bYEnt,
        float bXProj,
        float bYProj
    ) {
        this.controllerComponent.setStickOnCollision(stickOnCollision);
        this.controllerComponent.setStickToWall(stickOnWall);
        this.controllerComponent.setStickToCeiling(stickOnCeiling);
        this.controllerComponent.setStickToGround(stickOnGround);
        this.controllerComponent.setAffectedByGravity(affectedByGravity);
        this.controllerComponent.setColideWithSameTypeProjectiles(collideWithItself);
        this.controllerComponent.setColideWithOtherProjectiles(collideWithOtherProjectiles);

        this.controllerComponent.setBounceEnvironmentX(bXEnv);
        this.controllerComponent.setBounceEnvironmentY(bYEnv);

        this.controllerComponent.setBounceEntityX(bXEnt);
        this.controllerComponent.setBounceEntityY(bYEnt);

        this.controllerComponent.setBounceProjectileX(bXProj);
        this.controllerComponent.setBounceProjectileY(bYProj);
    }

    public abstract void onEnvironmentCollision(Contact contact, Object target);

    public abstract void onEnvironmentEndCollision(Contact contact, Object target);

    public abstract void onEntityCollision(Contact contact, Object target);

    public abstract void onEntityEndCollision(Contact contact, Object target);

    public abstract void onProjectileCollision(Contact contact, Object target);

    public abstract void onProjectileEndCollision(Contact contact, Object target);

    protected abstract void setBodyDefValues();

    /// Cria uma body padrão para todos os projéteis
    protected void createBody() {
        body = BodyCreatorHelper.createCircle(
            world,
            new Vector2(x, y),
            radius,
            BodyDef.BodyType.DynamicBody,
            defDens, // density
            defFric,   // friction
            defRest // restitution
        );
        body.setBullet(true); // Importante para colisões de alta velocidade
        body.setFixedRotation(true);
        body.setUserData(new FixtureType(FixtType.PROJECTILE, this));

    }

    public void update(float deltaTime) {
        if (!active) return;

        updateComponents(deltaTime);

        controllerComponent.resetCollisionCounters();

    }

    /// Atualiza todos os componentes existentes dentro do objeto
    protected void updateComponents(float delta) {
        for (Component component : components) {
            component.update(delta);
        }
    }

    /// Serve para "matar" o projétil, resetando as suas propriedades dinâmicas
    public void die() {
        this.reset();
    }

    /// Reset e desativação do projétil
    @Override
    public void reset() {
        this.setActive(false);
        this.owner = null;

        this.setLifeTime(0f);

        controllerComponent.reset();
        this.dispose();

    }


    public void render(SpriteBatch batch) {

    }

    /// Valida se o projétil deve ser destruído permanentemente,
    /// verifica se o tempo inativo foi atingido e também se podemos eliminar a body do mundo
    public boolean shouldBeDestroyedPermanently() {
        boolean recycleByTime = controllerComponent.getInactiveTime() >= controllerComponent.getInactiveTimeLimit();
        boolean recycleByWorld = world != null && !world.isLocked();

        return !active && recycleByTime && recycleByWorld;
    }

    /// Limpa os dados mais pesados que precisam ser reciclados períodicamente
    public void dispose() {
        if (spriteSheetDatahandler != null) {
            spriteSheetDatahandler.dispose();
        }
    }

    /// Destrói a body e a física do projétil permanentemente
    public void destroyPhysics() {
        if (body != null && world != null && !world.isLocked()) {
            world.destroyBody(body);
            this.body = null;
            this.world = null;
        }
    }

    /// Limpa todas as informações do projétil para preparar ele para ser eliminado permanentemente
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

    public ProjectilePhysicsComponent getPhysicsComponent() {
        return physicsComponent;
    }

    public ProjectileControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public float getRadius() {
        return radius;
    }

    public void setActive(boolean value) {
        this.active = value;
        body.setActive(value);
    }

    public boolean isActive() {
        return active;
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
