package official.sketchBook.projectiles_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.animation_related.SpriteSheetDataHandler;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.components_related.toUse_component.projectile.ProjectileControllerComponent;
import official.sketchBook.components_related.toUse_component.projectile.ProjectilePhysicsComponent;
import official.sketchBook.customComponents_related.CustomPool;
import official.sketchBook.gameObject_related.base_model.Entity;
import official.sketchBook.projectiles_related.util.ProjectilePool;
import official.sketchBook.util_related.enumerators.types.FixtType;
import official.sketchBook.util_related.helpers.body.BodyCreatorHelper;
import official.sketchBook.util_related.info.values.FixtureType;

import java.util.ArrayList;
import java.util.List;

public abstract class Projectile implements CustomPool.Poolable {

    //TODO:adicionar compatibilidade para rotação, tanto na renderização quando nos objetos

    /// Raio do projétil
    protected float radius;

    /// Posição do projétil em pixels
    protected float x, y;

    /// Rotação do projétil
    protected float r;

    /// Componente de controle, tem um em cada projétil
    protected ProjectileControllerComponent controllerComponent;

    /// Componente de física próprio do projétil
    private final ProjectilePhysicsComponent physicsComponent;

    /// Gerenciador de sprites
    protected SpriteSheetDataHandler spriteSheetDatahandler;

    /// Gerenciador de animações
    protected ObjectAnimationPlayer animationPlayer;

    /// Dono do projétil, apenas uma entidade pode usar um projétil
    protected Entity owner;

    /// Corpo do projétil
    protected Body body;

    /// Mundo físico externo onde o projétil está presente
    protected World world;

    /// Valores padrão para a criação da body
    protected float defFric, defRest, defDens;

    /// Pool dona do projétil
    protected ProjectilePool<?> ownerPool;

    /// Lista de componentes
    protected List<Component> components;

    /// Flag de estado de vida
    private boolean reset, active;

    /// Flag que diz se o projétil está virado para a direita ou não
    protected boolean facingForward;

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

    /**
     * Inicia as informações dinâmicas do projétil
     * é preciso passar o tempo de vida e outros valores que precisam ser iniciados dinamicamente
     */
    public void init(Entity owner) {
        this.owner = owner;
        this.reset = false;
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
     * @param canRotate                   se o projétil pode rotacionar por conta própria
     * @param bounceX                     constante de restituição do eixo X pra ambiente
     * @param bounceY                     constante de restituição do eixo Y pra ambiente
     */
    protected void initBodyBehavior(
        boolean stickOnCollision,
        boolean stickOnWall,
        boolean stickOnCeiling,
        boolean stickOnGround,
        boolean affectedByGravity,
        boolean collideWithItself,
        boolean collideWithOtherProjectiles,
        boolean canRotate,
        float bounceX,
        float bounceY
    ) {
        this.controllerComponent.setStickOnCollision(stickOnCollision);
        this.controllerComponent.setStickToWall(stickOnWall);
        this.controllerComponent.setStickToCeiling(stickOnCeiling);
        this.controllerComponent.setStickToGround(stickOnGround);
        this.controllerComponent.setAffectedByGravity(affectedByGravity);
        this.controllerComponent.setColideWithSameTypeProjectiles(collideWithItself);
        this.controllerComponent.setColideWithOtherProjectiles(collideWithOtherProjectiles);
        this.controllerComponent.setCanRotate(canRotate);

        this.controllerComponent.setBounceX(bounceX);
        this.controllerComponent.setBounceY(bounceY);
    }

    public abstract void onEnvironmentCollision(Contact contact, Object target);

    public abstract void onEnvironmentEndCollision(Contact contact, Object target);

    public abstract void onEntityCollision(Contact contact, Object target);

    public abstract void onEntityEndCollision(Contact contact, Object target);

    public abstract void onProjectileCollision(Contact contact, Object target);

    public abstract void onProjectileEndCollision(Contact contact, Object target);

    /// Iniciamos os valores padrão da body, mas cada projétil implementa por conta própria
    protected abstract void setBodyDefValues();

    /// Cria uma body padrão para todos os projéteis
    protected void createBody() {
        body = BodyCreatorHelper.createCircle(
            world,
            new Vector2(x, y),
            r,
            radius,
            BodyDef.BodyType.DynamicBody,
            defDens, // density
            defFric,   // friction
            defRest // restitution
        );
        body.setBullet(true); // Importante para colisões de alta velocidade
        body.setFixedRotation(controllerComponent.isCanRotate());
        body.setUserData(new FixtureType(FixtType.PROJECTILE, this));

    }

    public void update(float deltaTime) {
        updateComponents(deltaTime);
        controllerComponent.resetCollisionCounters();
    }

    /// Atualiza todos os componentes existentes dentro do objeto
    private void updateComponents(float delta) {
        if (!active) return;        // Nenhum componente precisa rodar se está inativo
        for (Component c : components) {
            c.update(delta);
        }
    }

    /// Reset e desativação do projétil junto da liberação da memória
    @Override
    public void reset() {
        if (reset) return;

        this.setActive(false);
        this.owner = null;
        this.setLifeTime(0f);
        controllerComponent.reset();

        this.dispose();

        reset = true;
    }

    public void render(SpriteBatch batch) {

    }

    /// Destrói a body e a física do projétil permanentemente dentro do mundo e a limpa da memória
    private void destroyPhysics() {
        if (body != null && world != null && !world.isLocked()) {
            world.destroyBody(body);
            this.body = null;
            this.world = null;
        }
    }

    /// Destrói todos os componentes antes de limpar a lista
    private void clearComponents() {
        components.replaceAll(ignored -> null);
        components.clear();
    }

    /// Limpa todas as informações do projétil após ser resetado, para ser destruído
    public void destroy() {
        if (!reset) return;
        destroyPhysics();
        clearComponents();
    }

    /// Limpa os dados mais pesados que precisam ser reciclados períodicamente
    public void dispose() {
        if (spriteSheetDatahandler != null) {
            spriteSheetDatahandler.dispose();
        }
    }

    public void setOwnerPool(ProjectilePool<?> ownerPool) {
        this.ownerPool = ownerPool;
    }

    /// Libera de dentro da pool
    public void release() {
        if (ownerPool != null) {
            ownerPool.free(this);
        }
    }

    public ProjectilePool<?> getOwnerPool() {
        return ownerPool;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setLifeTime(float lifeTime) {
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
        if (body != null) { // se houver uma instancia válida de body permitimos sua alteração
            body.setActive(value);
        }
    }

    public boolean isReset() {
        return reset;
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

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public Body getBody() {
        return body;
    }

    public World getWorld() {
        return world;
    }

    public void addComponent(Component comp) {
        components.add(comp);
    }

}
